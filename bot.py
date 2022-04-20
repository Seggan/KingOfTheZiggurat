import sys
import socket
import time

time.sleep(1)

port = int(sys.argv[1])

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect(('localhost', port))

while True:
    buffer = ""
    while True:
        data = socket.recv(1)
        if data.decode("utf-8") == '\n':
            buffer += data.decode("utf-8")
            break
        buffer += data.decode("utf-8")

    buffer = buffer.strip()
    print(buffer)
    if buffer == "tick":
        socket.send("NORTH\n".encode("utf-8"))
    elif buffer == "fight":
        socket.send("true\n".encode("utf-8"))