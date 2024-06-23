import socket
import threading
import subprocess
import signal
import sys
import random
import string
from typing import Tuple

def execute_command(input_data: str) -> Tuple[str, str]:
    process = subprocess.Popen(
        "./programaTrab",
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    stdout, stderr = process.communicate(input=input_data)
    return stdout, stderr

def parser(line: str) -> str:
    if len(line.split(' ')) < 2:
        return "ERROR Número errado de comandos\n"
    cmd = line.split(' ')[0]
    args = line.split(' ')[1:]

    print(f"Comando = {cmd}, args = {args}")

    match cmd:
        case "abrir":
            random_string = ''.join(random.choices(string.ascii_letters + string.digits, k=10))
            out, err = execute_command(f"1 {args[0]} {random_string}.bin")
            if out.startswith("Falha"):
                return "ERROR Falha na abertura do arquivo"
            return random_string
        case "busca":
            query = ' '.join(args[1:])
            out, err = execute_command(f"3 {args[0]}.bin 1\n1 {query}")
            return out + "END"
        case "buscatodos":
            out, err = execute_command(f"2 {args[0]}.bin")
            if out.startswith("Falha"):
                return "ERROR Falha na abertura do arquivo"
            return out + "END"
        case "deleta":
            out, err = execute_command(f"4 {args[0]}.bin {args[0]}.index.bin")
            out, err = execute_command(f"5 {args[0]}.bin {args[0]}.index.bin 1\n1 id {args[1]}")
            return out + "END"
        case "atualiza":
            out, err = execute_command(f"4 {args[0]}.bin {args[0]}.index.bin")
            out, err = execute_command(f"5 {args[0]}.bin {args[0]}.index.bin 1\n1 id {args[1]}")
            out, err = execute_command(f"6 {args[0]}.bin {args[0]}.index.bin 1\n1 id {args[1]} {args[2]} \"{args[3]}\" \"{args[4]}\" \"{args[5]}\"")
            return out + "END"

    return f"ERRO '{cmd}' não existe"

def handle_client(conn: socket.socket, addr: Tuple[str, int]):
    print('Connected by', addr)
    with conn:
        buffer = ""
        while True:
            data = conn.recv(1024).decode()
            if not data:
                break
            buffer += data
            while '\n' in buffer:
                line, buffer = buffer.split('\n', 1)
                conn.sendall((parser(line) + '\n').encode())

def main():
    port = 1337
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(('localhost', port))
    s.listen(5)
    print(f'Server is listening on port {port}...')

    def signal_handler(sig, frame):
        print('Shutting down server...')
        s.close()
        sys.exit(0)

    signal.signal(signal.SIGINT, signal_handler)

    while True:
        conn, addr = s.accept()
        threading.Thread(target=handle_client, args=(conn, addr)).start()

if __name__ == "__main__":
    main()
