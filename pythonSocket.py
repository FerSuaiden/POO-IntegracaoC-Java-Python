import socket
import subprocess
import threading
import os

def handle_client(client_socket):
    try:
        while True:
            request = client_socket.recv(1024).decode()
            if not request:
                break
            print(f"Received: {request}")

            response = process_request(request)
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
        bin_index_file = bin_file.replace(".bin", "_index.bin")
        print(f"Loading CSV: {csv_file} to Binary: {bin_file} and {bin_index_file}")
        try:
            # Create main .bin file
            process_main = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input_main = f"1 {csv_file} {bin_file}\n"
            stdout_main, stderr_main = process_main.communicate(input=command_input_main)

            if process_main.returncode != 0:
                return stderr_main

            # Create index .bin file
            process_index = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input_index = f"4 {bin_file} {bin_index_file}\n"
            stdout_index, stderr_index = process_index.communicate(input=command_input_index)

            if process_index.returncode != 0:
                return stderr_index

            return stdout_main + stdout_index
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
        except Exception as e:
            return f"Error: {str(e)}"

    elif command == "search":
        bin_file, num_searches, *search_fields = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input = f"3 {bin_file} {num_searches} {' '.join(search_fields)}\n"
            stdout, stderr = process.communicate(input=command_input)
            return stdout if process.returncode == 0 else stderr
        except Exception as e:
            return f"Error: {str(e)}"

    elif command == "create_index":
        bin_file, index_file, option = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        index_file = os.path.basename(index_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input = f"4 {bin_file} {index_file} {option}\n"
            stdout, stderr = process.communicate(input=command_input)
            return stdout if process.returncode == 0 else stderr
        except Exception as e:
            return f"Error: {str(e)}"

    elif command == "remove":
        bin_file, num_fields, *fields = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            command_input = f"5 {bin_file} {bin_file.replace('.bin', '_index.bin')} {num_fields} {' '.join(fields)}\n"
            print(f"Command input: {command_input}")
            stdout, stderr = process.communicate(input=command_input)
            print(f"Remove command stdout: {stdout}")
            print(f"Remove command stderr: {stderr}")
            return stdout if process.returncode == 0 else stderr
        except Exception as e:
            return f"Error: {str(e)}"

    elif command == "insert":
        bin_file, index_file, num_insertions, *insertion_fields = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        index_file = os.path.basename(index_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            insertion_cmd = f"6 {bin_file} {index_file} {num_insertions} {' '.join(insertion_fields)}\n"
            stdout, stderr = process.communicate(input=insertion_cmd)
            return stdout if process.returncode == 0 else stderr
        except Exception as e:
            return f"Error: {str(e)}"

    elif command == "edit":
        bin_file, old_name, new_name, new_nationality, new_club = args[1:]
        bin_file = os.path.basename(bin_file.strip())
        try:
            process = subprocess.Popen(["./programaTrab"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            edit_cmd = f"7 {bin_file} \"{old_name}\" \"{new_name}\" \"{new_nationality}\" \"{new_club}\"\n"
            stdout, stderr = process.communicate(input=edit_cmd)
            return stdout if process.returncode == 0 else stderr
        except Exception as e:
            return f"Error: {str(e)}"

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
