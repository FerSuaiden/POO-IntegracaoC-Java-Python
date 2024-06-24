all:
	gcc -g -Wall -o programaTrab *.c

run: all
	./programaTrab

clean:
	rm *.exe