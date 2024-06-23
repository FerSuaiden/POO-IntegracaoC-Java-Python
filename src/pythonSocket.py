import socket
import subprocess
import os

def handle_client(client_socket):
    request = client_socket.recv(1024).decode()
    print(f"Received request: {request}")
    command, *params = request.split(';')
    
    response = ""

    if command == 'load':
        csv_file, bin_file = params
        print(f"Loading CSV file: {csv_file} into binary file: {bin_file}")
        # Ensure the path to the CSV file is correct
        csv_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', csv_file)
        bin_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', bin_file)
        result = subprocess.run(['./programaTrab', 'criarArquivo', csv_path, bin_path], capture_output=True, text=True)
        if result.returncode == 0:
            response = "Binary file created successfully."
        else:
            response = f"Failed to create binary file: {result.stderr}"
    
    elif command == 'list':
        bin_file = params[0]
        print(f"Listing all players from binary file: {bin_file}")
        bin_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', bin_file)
        result = subprocess.run(['./programaTrab', 'listarRegistros', bin_path], capture_output=True, text=True)
        response = result.stdout
    
    elif command == 'search':
        bin_file, id, age, name, nationality, club = params
        print(f"Searching in binary file: {bin_file} with params id={id}, age={age}, name={name}, nationality={nationality}, club={club}")
        bin_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', bin_file)
        result = subprocess.run(['./programaTrab', 'buscarRegistros', bin_path, id, age, name, nationality, club], capture_output=True, text=True)
        response = result.stdout
    
    response += "<END_OF_MESSAGE>"
    client_socket.send(response.encode())
    client_socket.close()
    print(f"Response sent: {response}")

def main():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("localhost", 12345))
    server.listen(5)
    print("Server listening on port 12345...")
    
    while True:
        client_socket, addr = server.accept()
        print(f"Accepted connection from {addr}")
        handle_client(client_socket)

if __name__ == "__main__":
    main()
