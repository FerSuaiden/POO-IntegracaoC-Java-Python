import socket
import subprocess
import os

def handle_client(client_socket):
    request = client_socket.recv(1024).decode()
    print(f"Received request: {request}")
    command, *params = request.split(';')
    
    response = ""

    if command == 'criarArquivo':
        if len(params) != 2:
            response = "Invalid number of parameters for criarArquivo command."
        else:
            csv_file, bin_file = params
            csv_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', csv_file)
            bin_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', bin_file)
            try:
                result = subprocess.run(['./programaTrab', 'criarArquivo', csv_path, bin_path], capture_output=True, text=True)
                if result.returncode == 0:
                    response = "Binary file created successfully."
                else:
                    response = f"Failed to create binary file: {result.stderr}"
            except Exception as e:
                response = f"Error during criarArquivo command: {str(e)}"
    
    elif command == 'listarRegistros':
        if len(params) != 1:
            response = "Invalid number of parameters for listarRegistros command."
        else:
            bin_file = params[0]
            bin_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', bin_file)
            try:
                result = subprocess.run(['./programaTrab', 'listarRegistros', bin_path], capture_output=True, text=True)
                if result.returncode == 0:
                    response = result.stdout
                else:
                    response = f"Failed to list players: {result.stderr}"
            except Exception as e:
                response = f"Error during listarRegistros command: {str(e)}"

    elif command == 'buscarRegistros':
        if len(params) != 2:
            response = "Invalid number of parameters for buscarRegistros command."
        else:
            bin_file, num_buscas = params
            bin_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', bin_file)
            try:
                result = subprocess.run(['./programaTrab', 'buscarRegistros', bin_path, num_buscas], capture_output=True, text=True)
                if result.returncode == 0:
                    response = result.stdout
                else:
                    response = f"Failed to search players: {result.stderr}"
            except Exception as e:
                response = f"Error during buscarRegistros command: {str(e)}"

    elif command == 'criarIndex':
        if len(params) != 2:
            response = "Invalid number of parameters for criarIndex command."
        else:
            bin_file, index_file = params
            bin_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', bin_file)
            index_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', index_file)
            try:
                result = subprocess.run(['./programaTrab', 'criarIndex', bin_path, index_path], capture_output=True, text=True)
                if result.returncode == 0:
                    response = "Index file created successfully."
                else:
                    response = f"Failed to create index file: {result.stderr}"
            except Exception as e:
                response = f"Error during criarIndex command: {str(e)}"

    elif command == 'removerRegistros':
        if len(params) != 3:
            response = "Invalid number of parameters for removerRegistros command."
        else:
            bin_file, index_file, num_remocoes = params
            bin_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', bin_file)
            index_path = os.path.join('/home/coqzieiro/Área de Trabalho/poo/bin', index_file)
            try:
                result = subprocess.run(['./programaTrab', 'removerRegistros', bin_path, index_path, num_remocoes], capture_output=True, text=True)
                if result.returncode == 0:
                    response = "Records removed successfully."
                else:
                    response = f"Failed to remove records: {result.stderr}"
            except Exception as e:
                response = f"Error during removerRegistros command: {str(e)}"

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
