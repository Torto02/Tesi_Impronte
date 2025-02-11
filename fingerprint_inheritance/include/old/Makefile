#Makefile
OBJS	= $(BUILD_DIR)/main.o $(BUILD_DIR)/jsoncpp.o $(BUILD_DIR)/files.o \
		  $(BUILD_DIR)/fp_minutia.o $(BUILD_DIR)/fp_total_minutiae.o \
		  $(BUILD_DIR)/fp_edge.o $(BUILD_DIR)/fp_edges_table.o \
		  $(BUILD_DIR)/fp_edges_score_generator.o $(BUILD_DIR)/fp_edges_percentage_generator.o \
		  $(BUILD_DIR)/fp_edges_percentage_generator_plp.o \
		  $(BUILD_DIR)/matrix_from_file.o

CC = g++
CFLAGS = -g -Wall

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
$(BUILD_DIR)/matrix_from_file.o: $(INCLUDE_DIR)/matrix_from_file.cpp $(INCLUDE_DIR)/matrix_from_file.h $(LIB_DIR)/json/include/json.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/matrix_from_file.cpp -o $(BUILD_DIR)/matrix_from_file.o

$(BUILD_DIR)/files.o: $(INCLUDE_DIR)/files.cpp $(INCLUDE_DIR)/files.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/files.cpp -o $(BUILD_DIR)/files.o

$(BUILD_DIR)/fp_minutia.o: $(INCLUDE_DIR)/fp_minutia.cpp $(INCLUDE_DIR)/fp_minutia.h $(LIB_DIR)/json/include/json.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/fp_minutia.cpp -o $(BUILD_DIR)/fp_minutia.o

$(BUILD_DIR)/fp_total_minutiae.o: $(INCLUDE_DIR)/fp_total_minutiae.cpp $(INCLUDE_DIR)/fp_total_minutiae.h $(INCLUDE_DIR)/fp_minutia.h $(INCLUDE_DIR)/matrix_from_file.h $(LIB_DIR)/json/include/json.h 
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/fp_total_minutiae.cpp -o $(BUILD_DIR)/fp_total_minutiae.o
	
$(BUILD_DIR)/fp_edge.o: $(INCLUDE_DIR)/fp_edge.cpp $(INCLUDE_DIR)/fp_edge.h $(INCLUDE_DIR)/fp_minutia.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/fp_edge.cpp -o $(BUILD_DIR)/fp_edge.o
	
$(BUILD_DIR)/fp_edges_table.o:	include/fp_edges_table.cpp $(INCLUDE_DIR)/fp_edges_table.h $(LIB_DIR)/json/include/json.h $(INCLUDE_DIR)/fp_minutia.h $(INCLUDE_DIR)/fp_total_minutiae.h $(INCLUDE_DIR)/fp_edge.h
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/fp_edges_table.cpp -o $(BUILD_DIR)/fp_edges_table.o

$(BUILD_DIR)/fp_edges_score_generator.o: $(INCLUDE_DIR)/fp_edges_score_generator.cpp $(INCLUDE_DIR)/fp_edges_score_generator.h $(LIB_DIR)/json/include/json.h $(INCLUDE_DIR)/fp_minutia.h $(INCLUDE_DIR)/fp_total_minutiae.h $(INCLUDE_DIR)/fp_edges_table.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/fp_edges_score_generator.cpp -o $(BUILD_DIR)/fp_edges_score_generator.o

$(BUILD_DIR)/fp_edges_percentage_generator.o: $(INCLUDE_DIR)/fp_edges_percentage_generator.cpp $(INCLUDE_DIR)/fp_edges_percentage_generator.h $(LIB_DIR)/json/include/json.h $(INCLUDE_DIR)/fp_minutia.h $(INCLUDE_DIR)/fp_total_minutiae.h $(INCLUDE_DIR)/fp_edge.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/fp_edges_percentage_generator.cpp -o $(BUILD_DIR)/fp_edges_percentage_generator.o
	
$(BUILD_DIR)/fp_edges_percentage_generator_plp.o: $(INCLUDE_DIR)/fp_edges_percentage_generator_plp.cpp $(INCLUDE_DIR)/fp_edges_percentage_generator_plp.h $(LIB_DIR)/json/include/json.h $(INCLUDE_DIR)/fp_minutia.h $(INCLUDE_DIR)/fp_total_minutiae.h $(INCLUDE_DIR)/fp_edge.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c $(INCLUDE_DIR)/fp_edges_percentage_generator_plp.cpp -o $(BUILD_DIR)/fp_edges_percentage_generator_plp.o

#Main file
$(BUILD_DIR)/main.o: src/main.cpp $(INCLUDE_DIR)/files.h $(INCLUDE_DIR)/fp_edges_score_generator.h $(INCLUDE_DIR)/fp_edges_percentage_generator.h $(INCLUDE_DIR)/fp_edges_percentage_generator_plp.h
	$(dir_guard)
	$(CC) $(CFLAGS) -c src/main.cpp -o $(BUILD_DIR)/main.o

clean:
	$(dir_guard)
	rm -f $(BUILD_DIR)/*.o $(BIN_DIR)/*.out