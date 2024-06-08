import serial
import time

ser = serial.Serial('COM1', 19200)

while True:
    data = "Hello Arduino\n"
    ser.write(data.encode())
    print(f"Sent: {data}")
    time.sleep(2)