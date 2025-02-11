#Makefile
OBJS	= $(BUILD_DIR)/main.o \
		  $(BUILD_DIR)/jsoncpp.o \
		  $(BUILD_DIR)/files.o \
		  $(BUILD_DIR)/minutia.o $(BUILD_DIR)/edge.o $(BUILD_DIR)/triangle.o $(BUILD_DIR)/skeleton.o \
		  $(BUILD_DIR)/fingerprint.o

CC = g++
CFLAGS = -std=c++11 -g -Wall -Wfatal-errors

dir_guard = @mkdir -p $(@D)
BUILD_DIR = build
BIN_DIR = bin
LIB_DIR = lib
INCLUDE_DIR = include

$(BIN_DIR)/fp.out:		$(OBJS)
		$(dir_guard)
		$(CC) $(CFLAGS) $(OBJS) -o $(BIN_DIR)/fp.out

#External $(LIB_DIR)raries
$(BUILD_DIR)/jsoncpp.o: $(LIB_DIR)/json/jsoncpp.cpp $(LIB_DIR)/json/include/json.h  $(LIB_DIR)/json/include/json-forwards.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(LIB_DIR)/json/jsoncpp.cpp -o $(BUILD_DIR)/jsoncpp.o

#My dependencies
$(BUILD_DIR)/files.o: $(INCLUDE_DIR)/files.cpp $(INCLUDE_DIR)/files.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/files.cpp -o $(BUILD_DIR)/files.o

$(BUILD_DIR)/minutia.o: $(INCLUDE_DIR)/minutia.cpp $(INCLUDE_DIR)/minutia.h \
			   $(LIB_DIR)/json/include/json.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/minutia.cpp -o $(BUILD_DIR)/minutia.o

$(BUILD_DIR)/edge.o: $(INCLUDE_DIR)/edge.cpp $(INCLUDE_DIR)/edge.h \
		     $(INCLUDE_DIR)/minutia.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/edge.cpp -o $(BUILD_DIR)/edge.o
	
$(BUILD_DIR)/triangle.o: $(INCLUDE_DIR)/triangle.cpp $(INCLUDE_DIR)/triangle.h \
		     $(INCLUDE_DIR)/skeleton.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/triangle.cpp -o $(BUILD_DIR)/triangle.o
	
$(BUILD_DIR)/skeleton.o: $(INCLUDE_DIR)/skeleton.cpp $(INCLUDE_DIR)/skeleton.h \
			 $(LIB_DIR)/json/include/json.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/skeleton.cpp -o $(BUILD_DIR)/skeleton.o
	
$(BUILD_DIR)/fingerprint.o: $(INCLUDE_DIR)/fingerprint.cpp $(INCLUDE_DIR)/fingerprint.h \
			    $(INCLUDE_DIR)/minutia.h $(INCLUDE_DIR)/edge.h $(INCLUDE_DIR)/skeleton.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/fingerprint.cpp -o $(BUILD_DIR)/fingerprint.o


#Main file
$(BUILD_DIR)/main.o: src/main.cpp \
		     $(INCLUDE_DIR)/files.h $(INCLUDE_DIR)/fingerprint.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c src/main.cpp -o $(BUILD_DIR)/main.o

clean:
	$(dir_guard)
	rm -f $(BUILD_DIR)/*.o $(BIN_DIR)/*.out
