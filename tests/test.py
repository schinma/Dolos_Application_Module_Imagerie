import socket
import json
import sys
import threading

packet_begin_str = "<Dolos_STR>"
packet_end_str = "</Dolos_STR>"

class ReadingThread(threading.Thread):
    def __init__(self, s, l):
        threading.Thread.__init__(self)
        self.running = True
        self.buffer = str("")
        self.s = s
        self.imagery_started = False
        self.view_live = False
        self.labels = l

    def read_socket(self):
        """
        :type s: socket.socket
        """
        while True:
            try:
                data = self.s.recv(1024)
                buff = data.decode()
            except IOError as err:
                return None
            print(buff)
            self.buffer = self.buffer + buff
            if self.buffer.find(packet_begin_str) != -1 and self.buffer.rfind(packet_end_str) != -1:
                break
        #print(self.buffer)
        packet_begin = self.buffer.find(packet_begin_str) + len(packet_begin_str)
        packet_end = self.buffer.rfind(packet_end_str)
        packet = self.buffer[packet_begin:packet_end]
        self.buffer = self.buffer[packet_end + len(packet_end_str):]
        return json.loads(packet)

    def run(self):
        while self.running:
            p = self.read_socket()
            if p is None:
                continue
            packet_name = p['packet']
            if packet_name == "Imagery_Infos_Packet":
                data = json.loads(p['data'])
                self.imagery_started = data['status']
                self.labels = data['labels']
                print("Imagery labels : " + str(self.labels))
                print("Imagery status : " + str(self.imagery_started))
            elif packet_name == "Control_Imagery_Packet":
                data = json.loads(p['data'])
                self.imagery_started = data['status']
                print("Imagery status : " + str(self.imagery_started))
            elif packet_name == "View_Live_Control_Packet":
                data = json.loads(p['data'])
                self.view_live = data['status']
                print("Live view status : " + str(self.view_live))
            elif packet_name == "Labeled_Frame_Packet":
                data = str(p['data'])
                label, frame = data.split(',')
                print("Imagery find a " + label)
            elif packet_name == "Frame_Packet" and self.view_live:
                print("Live Frame")

def control_imagery(s, value):
    """
    :type s: socket.socket
    :type value: bool
    """
    p = {
        "packet": "Control_Imagery_Packet",
        "data": str(json.dumps({"status": value}))
    }
    packet = packet_begin_str + json.dumps(p) + packet_end_str
    s.send(packet)


def label_selection(s, labels):
    """
    :type s: socket.socket
    """
    p = {
        "packet": "Label_Selection_Packet",
        "data": str(json.dumps({"labels": labels}))
    }
    packet = packet_begin_str + json.dumps(p) + packet_end_str
    s.send(packet)


def control_live(s, value):
    """
    :type s: socket.socket
    :type value: bool
    """
    p = {
        "packet": "View_Live_Control_Packet",
        "data": str(json.dumps({"status": value}))
    }
    packet = packet_begin_str + json.dumps(p) + packet_end_str
    s.send(packet)

def imagery_infos(s, labels, value):
    """
    :type s: socket.socket
    :type value: bool
    """
    p = {
        "packet": "Imagery_Infos_Packet",
        "data": str(json.dumps({"labels": labels, "status" : value}))
    }
    packet = packet_begin_str + json.dumps(p) + packet_end_str
    s.send(packet.encode())


def check_correct_labels(ref_labels, labels):
    for l in labels:
        if l not in read_thread.labels:
            print("The label \"" + l + "\" is not implemented.")
            return False
    return True


def remove_double_label(labels):
    tmp_labels = []
    for l in labels:
        if l not in tmp_labels:
            tmp_labels.append(l)
    return tmp_labels


s = socket.socket()
host = '127.0.0.1'
port = 2544

s.bind((host,port))
s.listen()

client, addr = s.accept()

print('connected with :', addr)

read_thread = ReadingThread(client, ['dog', 'cat', 'cow'])

read_thread.start()

while True:
    line = str(sys.stdin.readline())
    if line.endswith('\n'):
        line = line[:len(line) - 1]
    if line.startswith('quit') or len(line) == 0:
        break
    elif line.startswith('infos'):
        labels = line.split(' ')[1:]
        imagery_infos(client, read_thread.labels, False)
    elif line.startswith('start'):
        if line.endswith('imagery') and not read_thread.imagery_started:
            control_imagery(client, True)
        elif line.endswith('live') and not read_thread.view_live:
            control_live(client, True)
    elif line.startswith('stop'):
        if line.endswith('imagery') and read_thread.imagery_started:
            control_imagery(client, False)
        elif line.endswith('live') and read_thread.view_live:
            control_live(client, False)
    elif line.startswith('select'):
        labels = line.split(' ')[1:]
        if not check_correct_labels(read_thread.labels, labels):
            continue
        labels = remove_double_label(labels)
        print("Selected labels : " + str(labels))
        label_selection(client, labels)

client.close()
s.close()
read_thread.running = False
read_thread.join()
