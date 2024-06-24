import socket
import subprocess
import threading
import os

def handle_client(client_socket):
    try:
        request = client_socket.recv(1024).decode()
        print(f"Received: {request}")

        response = process_request(request)
        
        # Debug print for response
        print(f"Sending response: {response}")

        client_socket.sendall((response + "<END_OF_MESSAGE>\n").encode())
    except Exception as e:
        client_socket.sendall(f"Error: {str(e)}".encode())
    finally:
        client_socket.close()

def process_request(request):
    args = request.split(';')
    command = args[0]

    if command == "load":
        _, csv_file, bin_file = args

        csv_file = os.path.basename(csv_file.strip())
        bin_file = os.path.basename(bin_file.strip())

        print(f"Loading CSV: {csv_file} to Binary: {bin_file}")

        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input = f"1 {csv_file} {bin_file}\n"
            stdout, stderr = process.communicate(input=command_input)

            if process.returncode != 0:
                return stderr

            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            list_command_input = f"2 {bin_file}\n"
            list_stdout, list_stderr = process.communicate(input=list_command_input)

            return list_stdout if process.returncode == 0 else list_stderr

        except subprocess.CalledProcessError as e:
            return f"Error: {e.stderr}"
        except Exception as e:
            return f"Error: {str(e)}"

    elif command == "list":
        _, bin_file = args
        bin_file = os.path.basename(bin_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input = f"2 {bin_file}\n"
            stdout, stderr = process.communicate(input=command_input)
            return stdout if process.returncode == 0 else stderr
        except subprocess.CalledProcessError as e:
            return f"Error: {e.stderr}"

    elif command == "search":
        bin_file, num_searches, *search_fields = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input = f"3 {bin_file} {num_searches} {' '.join(search_fields)}\n"
            stdout, stderr = process.communicate(input=command_input)
            return stdout if process.returncode == 0 else stderr
        except subprocess.CalledProcessError as e:
            return f"Error: {e.stderr}"

    elif command == "create_index":
        bin_file, index_file, option = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        index_file = os.path.basename(index_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input = f"4 {bin_file} {index_file} {option}\n"
            stdout, stderr = process.communicate(input=command_input)
            return stdout if process.returncode == 0 else stderr
        except subprocess.CalledProcessError as e:
            return f"Error: {e.stderr}"

    elif command == "remove":
        bin_file, index_file, num_removals, *removal_fields = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        index_file = os.path.basename(index_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            removal_cmd = f"5 {bin_file} {index_file} {num_removals} {' '.join(removal_fields)}\n"
            stdout, stderr = process.communicate(input=removal_cmd)
            return stdout if process.returncode == 0 else stderr
        except subprocess.CalledProcessError as e:
            return f"Error: {e.stderr}"

    elif command == "insert":
        bin_file, index_file, num_insertions, *insertion_fields = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        index_file = os.path.basename(index_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            insertion_cmd = f"6 {bin_file} {index_file} {num_insertions} {' '.join(insertion_fields)}\n"
            stdout, stderr = process.communicate(input=insertion_cmd)
            return stdout if process.returncode == 0 else stderr
        except subprocess.CalledProcessError as e:
            return f"Error: {e.stderr}"

    else:
        return "Invalid command"

def main():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("localhost", 12345))
    server.listen(5)
    print("Server listening on port 12345")

    while True:
        client_socket, addr = server.accept()
        client_handler = threading.Thread(target=handle_client, args=(client_socket,))
        client_handler.start()

if __name__ == "__main__":
    main()
