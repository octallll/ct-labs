/**
    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
    ░░░░░░░░░░▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄░░░░░░░░░
    ░░░░░░░░▄▀░░░░░░░░░░░░▄░░░░░░░▀▄░░░░░░░
    ░░░░░░░░█░░▄░░░░▄░░░░░░░░░░░░░░█░░░░░░░
    ░░░░░░░░█░░░░░░░░░░░░▄█▄▄░░▄░░░█░▄▄▄░░░
    ░▄▄▄▄▄░░█░░░░░░▀░░░░▀█░░▀▄░░░░░█▀▀░██░░
    ░██▄▀██▄█░░░▄░░░░░░░██░░░░▀▀▀▀▀░░░░██░░
    ░░▀██▄▀██░░░░░░░░▀░██▀░░░░░░░░░░░░░▀██░
    ░░░░▀████░▀░░░░▄░░░██░░░▄█░░░░▄░▄█░░██░
    ░░░░░░░▀█░░░░▄░░░░░██░░░░▄░░░▄░░▄░░░██░
    ░░░░░░░▄█▄░░░░░░░░░░░▀▄░░▀▀▀▀▀▀▀▀░░▄▀░░
    ░░░░░░█▀▀█████████▀▀▀▀████████████▀░░░░
    ░░░░░░████▀░░███▀░░░░░░▀███░░▀██▀░░░░░░
    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
**/

#include <iostream>
#include <vector>
#include "CommandInformation.h"
#include <cstdio>
#include <sstream>
#include <algorithm>
#include <cassert>
#include <fstream>
#include "LRUandPLRU.h"

using namespace std;

enum ArgumentType {
    Register,
    Const,
    FenceArg
};

vector<ArgumentType> getExpectedArguments(string commandName) {
    CommandType type = commandToType[commandName];

    if (type == R) {
        return {Register, Register, Register};
    }

    if (type == S) {
        return {Register, Const, Register};
    }

    if (type == B) {
        return {Register, Register, Const};
    }

    if (type == J) {
        return {Register, Const};
    }

    if (type == U) {
        return {Register, Const};
    }

    if (type == I) {
        if (commandName == "ECALL" || commandName == "EBREAK") {
            return {};
        } else if (commandName == "JALR") {
            return {Register, Const, Register};
        } else if (commandName == "LB" || commandName == "LH" || commandName == "LW" || commandName == "LBU" || commandName == "LHU") {
            return {Register, Const, Register};
        } else {
            return {Register, Register, Const};
        }
    }

    if (type == Fence) {
        return {FenceArg, FenceArg};
    }

    if (type == FenceI) {
        return {};
    }

    return {};
}

int getIntegerValue(string value, int lastBit) {
    int integerValue;

    if (value.size() > 2 && value.substr(0, 2) == "0x") {
        integerValue = stoi(value, nullptr, 16);

        if (lastBit < 31 && (integerValue >> lastBit) & 1) {
            integerValue -= 2 * (1 << lastBit);
        }
    } else {
        integerValue = stoi(value);
    }

    return integerValue;
}

bool checkForFenceArg(string arg) {
    bool correct = true;

    for (char value : arg) {
        correct |= (value == 'i' || value == 'r' || value == 'w' || value == 'o');
    }

    return correct;
}

vector<vector<string>> readCommands() {
    vector<vector<string>> commands;

    string line;

    while (getline(cin, line)) {
        stringstream inLine(line);

        if (line.empty()) continue;

        vector<string> command;

        string commandName;
        inLine >> commandName;

        if (commandName.empty()) continue;

        for (char &symbol : commandName) symbol = toupper(symbol);

        if (!commandToType.count(commandName)) {
            cerr << "Incorrect command: " + commandName + "\n";
            exit(0);
        }

        command.push_back(commandName);

        char next;
        string arg;

        vector<string> args;

        while (inLine >> next) {
            if (next == ',') {
                args.push_back(arg);
                arg = "";
                continue;
            }

            if (next == ' ') continue;

            arg += next;
        }

        args.push_back(arg);

        vector<ArgumentType> expectedArguments = getExpectedArguments(commandName);

        if (args.size() != expectedArguments.size()) {
            cerr << "Incorrect number of arguments in command " + commandName << endl;
            exit(0);
        }

        for (int argIndex = 0; argIndex < expectedArguments.size(); argIndex++) {
            if (expectedArguments[argIndex] == Register) {
                if (!asmRegisterToBinRegister.count(args[argIndex])) {
                    cerr << "Error in arguments of command " + commandName + " expected register found: " + args[argIndex] << endl;
                    exit(0);
                }
            } else if (expectedArguments[argIndex] == Const) {
                try {
                    getIntegerValue(args[argIndex], lastBitInImm[commandToType[commandName]]);
                } catch (invalid_argument) {
                    cerr << "Error in arguments of command " + commandName + " expected const found: " + args[argIndex] << endl;
                    exit(0);
                }
            } else if (expectedArguments[argIndex] == FenceArg) {
                try {
                    getIntegerValue(args[argIndex], lastBitInImm[commandToType[commandName]]);
                } catch (invalid_argument) {
                    if (!checkForFenceArg(args[argIndex])) {
                        cerr << "Error in arguments of command " + commandName + " expected Fence argument found: " + args[argIndex] << endl;
                        exit(0);
                    }
                }
            }
        }

        for (string arg : args) {
            command.push_back(arg);
        }

        commands.push_back(command);
    }

    return commands;
}

string getFromIntInBin(string value, int startBit, int finishBit) {
    string result = "";

    int integerValue = getIntegerValue(value, 31);

    for (int bit = startBit; bit <= finishBit; bit++) {
        if ((integerValue >> bit) & 1) {
            result += '1';
        } else {
            result += '0';
        }
    }

    reverse(result.begin(), result.end());

    return result;
}

char getBitFromIntInBin(string value, int bit) {
    int integerValue = getIntegerValue(value, 31);

    return char((integerValue >> bit) & 1 + '0');
}

string getUType(vector<string> command) {
    string result = getFromIntInBin(command[2], 12, 31);

    return result + asmRegisterToBinRegister[command[1]] + commandToOpcode[command[0]];
}

string getJType(vector<string> command) {
    string result = getBitFromIntInBin(command[2], 20) + getFromIntInBin(command[2], 1, 10) + getBitFromIntInBin(command[2], 11) +
                    getFromIntInBin(command[2], 12, 19);

    result += asmRegisterToBinRegister[command[1]] + commandToOpcode[command[0]];

    return result;
}

string getBType(vector<string> command) {
    string result = getBitFromIntInBin(command[3], 12) + getFromIntInBin(command[3], 5, 10);

    result += asmRegisterToBinRegister[command[2]];
    result += asmRegisterToBinRegister[command[1]];

    result += commandToFunct3[command[0]];

    result += getFromIntInBin(command[3], 1, 4) + getBitFromIntInBin(command[3], 11);

    result += commandToOpcode[command[0]];

    return result;
}

string getSType(vector<string> command) {
    string result = getFromIntInBin(command[2], 5, 11);

    result += asmRegisterToBinRegister[command[1]];
    result += asmRegisterToBinRegister[command[3]];

    result += commandToFunct3[command[0]];

    result += getFromIntInBin(command[2], 0, 4);

    result += commandToOpcode[command[0]];

    return result;
}

string getRType(vector<string> command) {
    string result = commandToFunct7[command[0]];

    result += asmRegisterToBinRegister[command[3]];
    result += asmRegisterToBinRegister[command[2]];

    result += commandToFunct3[command[0]];

    result += asmRegisterToBinRegister[command[1]];

    result += commandToOpcode[command[0]];

    return result;
}

string getIType(vector<string> command) {
    string commandName = command[0];

    string result;

    if (commandName == "LB" || commandName == "LH" || commandName == "LW" || commandName == "LBU" || commandName == "LHU" || commandName == "JALR") {
        result = getFromIntInBin(command[2], 0, 11);

        result += asmRegisterToBinRegister[command[3]];

        result += commandToFunct3[command[0]];

        result += asmRegisterToBinRegister[command[1]];

        result += commandToOpcode[command[0]];
    } else if (commandName == "ADDI" || commandName == "SLTI" || commandName == "SLTIU" || commandName == "XORI" || commandName == "ORI" || commandName == "ANDI") {
        result = getFromIntInBin(command[3], 0, 11);

        result += asmRegisterToBinRegister[command[2]];

        result += commandToFunct3[command[0]];

        result += asmRegisterToBinRegister[command[1]];

        result += commandToOpcode[command[0]];
    } else if (commandName == "SLLI" || commandName == "SRLI" || commandName == "SRAI") {
        result += commandToFunct7[command[0]];

        result += getFromIntInBin(command[3], 0, 4);

        result += asmRegisterToBinRegister[command[2]];

        result += commandToFunct3[command[0]];

        result += asmRegisterToBinRegister[command[1]];

        result += commandToOpcode[command[0]];
    }  else if (commandName == "ECALL" || commandName == "EBREAK") {
        if (commandName == "ECALL") {
            result += "000000000000";
        } else {
            result += "000000000001";
        }

        result += "00000000000001110011";
    } else if (commandName == "CSRRW" || commandName == "CSRRS" || commandName == "CSRRC") {
        result += getFromIntInBin(command[2], 0, 11);

        result += asmRegisterToBinRegister[command[3]];

        result += commandToFunct3[command[0]];

        result += asmRegisterToBinRegister[command[1]];

        result += commandToOpcode[command[0]];
    } else {
        result += getFromIntInBin(command[2], 0, 11);

        result += getFromIntInBin(command[3], 0, 4);

        result += commandToFunct3[command[0]];

        result += asmRegisterToBinRegister[command[1]];

        result += commandToOpcode[command[0]];
    }

    return result;
}

string getFenceArgValue(string arg) {
    string result = "0000";

    for (char value : arg) {
        switch (value) {
            case 'w':
                result[3] = '1';
                break;
            case 'r':
                result[2] = '1';
                break;
            case 'o':
                result[1] = '1';
                break;
            case 'i':
                result[0] = '1';
                break;
        }
    }

    return result;
}

string getFenceType(vector<string> command) {
    string result = "0000";

    if (checkForFenceArg(command[1])) {
        result += getFenceArgValue(command[1]);
    } else {
        result += getFromIntInBin(command[1], 0, 3);
    }

    if (checkForFenceArg(command[2])) {
        result += getFenceArgValue(command[2]);
    } else {
        result += getFromIntInBin(command[2], 0, 3);
    }

    result += "00000000000000001111";

    return result;
}

string getFenceIType(vector<string> command) {
    string result =  "00000000000000000001000000001111";

    return result;
}

vector<string> commandsToBin(vector<vector<string>> &commands) {
    vector<string> binCommands;

    for (vector<string> command : commands) {
        string result;

        string commandName = command[0];

        if (commandToType[commandName] == R) {
            result = getRType(command);
        }

        if (commandToType[commandName] == I) {
            result = getIType(command);
        }

        if (commandToType[commandName] == S) {
            result = getSType(command);
        }

        if (commandToType[commandName] == B) {
            result = getBType(command);
        }

        if (commandToType[commandName] == U) {
            result = getUType(command);
        }

        if (commandToType[commandName] == J) {
            result = getJType(command);
        }

        if (commandToType[commandName] == Fence) {
            result = getFenceType(command);
        }

        if (commandToType[commandName] == FenceI) {
            result = getFenceIType(command);
        }

        binCommands.push_back(result);
    }

    return binCommands;
}

vector<vector<string>> getBinCommands(vector<string> commands) {
    vector<vector<string>> binCommands;

    for (string command : commands) {
        assert(command.size() == 32);

        vector<string> currentBinCommand;

        for (int startPos = 0; startPos < 32; startPos += 8) {
            string byte;

            for (int step = 0; step < 2; step++) {
                string current = command.substr(startPos + 4 * step, 4);

                int result = 0, pw = 1;

                while (!current.empty()) {
                    if (current.back() == '1') {
                        result += pw;
                    }

                    pw *= 2;
                    current.pop_back();
                }

                if (result < 10) {
                    byte += to_string(result);
                } else {
                    byte += char(result - 10 + 'a');
                }
            }

            currentBinCommand.push_back(byte);
        }

        reverse(currentBinCommand.begin(), currentBinCommand.end());

        binCommands.push_back(currentBinCommand);
    }

    return binCommands;
}

void getArguments(vector<string> command, int &rd, int &rs1, int &rs2, int &imm, int &shamt, int &csr) {
    string commandName = command[0];

    CommandType type = commandToType[command[0]];

    if (type == R) {
        rd = asmRegisterToIndexRegister[command[1]];
        rs1 = asmRegisterToIndexRegister[command[2]];
        rs2 = asmRegisterToIndexRegister[command[3]];
    }

    if (type == I) {
        if (commandName == "LB" || commandName == "LH" || commandName == "LW" || commandName == "LBU" || commandName == "LHU" || commandName == "JALR") {
            rd = asmRegisterToIndexRegister[command[1]];
            imm = getIntegerValue(command[2], lastBitInImm[commandToType[commandName]]);
            rs1 = asmRegisterToIndexRegister[command[3]];
        } else if (commandName == "ADDI" || commandName == "SLTI" || commandName == "SLTIU" || commandName == "XORI" || commandName == "ORI" || commandName == "ANDI") {
            rd = asmRegisterToIndexRegister[command[1]];
            rs1 = asmRegisterToIndexRegister[command[2]];
            imm = getIntegerValue(command[3], lastBitInImm[commandToType[commandName]]);
        } else if (commandName == "SLLI" || commandName == "SRLI" || commandName == "SRAI") {
            rd = asmRegisterToIndexRegister[command[1]];
            rs1 = asmRegisterToIndexRegister[command[2]];
            shamt = getIntegerValue(command[3], lastBitInImm[commandToType[commandName]]);
        } else if (commandName == "ECALL" || commandName == "EBREAK" || commandName == "FENCE" || commandName == "FENCEI") {
            // nop
        } else if (commandName == "CSRRW" || commandName == "CSRRS" || commandName == "CSRRC") {
            rd = asmRegisterToIndexRegister[command[1]];
            csr = getIntegerValue(command[2], lastBitInImm[commandToType[commandName]]);
            rs1 = asmRegisterToIndexRegister[command[3]];
        } else {
            rd = asmRegisterToIndexRegister[command[1]];
            csr = getIntegerValue(command[2], lastBitInImm[commandToType[commandName]]);
            imm = getIntegerValue(command[3], 4);
        }
    }

    if (type == B) {
        rs1 = asmRegisterToIndexRegister[command[1]];
        rs2 = asmRegisterToIndexRegister[command[2]];
        imm = getIntegerValue(command[3], lastBitInImm[commandToType[commandName]]);
    }

    if (type == U) {
        rd = asmRegisterToIndexRegister[command[1]];
        imm = getIntegerValue(command[2], lastBitInImm[commandToType[commandName]]);
    }

    if (type == J) {
        rd = asmRegisterToIndexRegister[command[1]];
        imm = getIntegerValue(command[2], lastBitInImm[commandToType[commandName]]);
    }

    if (type == S) {
        rd = asmRegisterToIndexRegister[command[1]];
        imm = getIntegerValue(command[2], lastBitInImm[commandToType[commandName]]);
        rs1 = asmRegisterToIndexRegister[command[3]];
    }
}

vector<double> simulateCache(vector<vector<string>> commands) {
    vector<int> memoryLRU(Consts.MEM_SIZE);
    vector<int> memoryPLRU(Consts.MEM_SIZE);

    vector<int> registerData(32, 0);

    LRUCache LRU;
    pLRUCache pLRU;

    auto getLineData = [&] (int address, vector<int> &memory) {
        int start = address - address % Consts.CACHE_LINE_SIZE;

        vector<int> data(Consts.CACHE_LINE_SIZE);

        for (int memIndex = start; memIndex < start + Consts.CACHE_LINE_SIZE; memIndex++) {
            data[memIndex - start] = memory[memIndex];
        }

        return data;
    };

    auto pickCache = [&] (int address, DataType type) {
        if (!LRU.memoryGet(address, type)) {
            LRU.insertData(address, getLineData(address, memoryLRU), memoryLRU);
        }

        if (!pLRU.memoryGet(address, type)) {
            pLRU.insertData(address, getLineData(address, memoryLRU), memoryPLRU);
        }
    };

    auto updateData = [&] (int address, int newData) {
        pickCache(address, Data);

        LRU.updateData(address, newData);
        pLRU.updateData(address, newData);
    };

    auto getData = [&] (int address, bool willPick) {
        if (address < 0 || address >= Consts.MEM_SIZE) {
            return -1;
        }

        if (willPick) {
            pickCache(address, Data);
        }

        int data = LRU.getData(address);

        if (data == INT_MAX) {
            data = memoryLRU[address];
        }

        return data;
    };

    auto deleteBits = [&] (int &value, int startBit, int finishBit) {
        for (int bit = startBit; bit <= finishBit; bit++) {
            cout << "bit: " << bit << endl;

            if ((value >> bit) & 1) {
                value -= (1 << bit);
            }
        }
    };

    int PC = getIntegerValue("0x10000", 31);
    int startPC = PC;

    registerData[asmRegisterToIndexRegister["ra"]] = -1;

    while (startPC <= PC && (PC - startPC) / 4 < commands.size() && getData(registerData[asmRegisterToIndexRegister["ra"]], false) != PC) {
        pickCache(startPC + PC, Instruction);

        vector<string> command = commands[(PC - startPC) / 4];

        registerData[0] = 0;

        string commandName = command[0];

        int rd, rs1, rs2, imm, shamt, csr;
        getArguments(command, rd, rs1, rs2, imm, shamt, csr);

        if (commandName == "ADD") {
            registerData[rd] = registerData[rs1] + registerData[rs2];
        }

        if (commandName == "SUB") {
            registerData[rd] = registerData[rs1] - registerData[rs2];
        }

        if (commandName == "XOR") {
            registerData[rd] = registerData[rs1] ^ registerData[rs2];
        }

        if (commandName == "OR") {
            registerData[rd] = registerData[rs1] | registerData[rs2];
        }

        if (commandName == "AND") {
            registerData[rd] = registerData[rs1] & registerData[rs2];
        }

        if (commandName == "SLL") {
            registerData[rd] = registerData[rs1] << registerData[rs2];
        }

        if (commandName == "SRL" || commandName == "SRA") {
            registerData[rd] = registerData[rs1] >> registerData[rs2];
        }

        if (commandName == "SLT") {
            registerData[rd] = registerData[rs1] < registerData[rs2] ? 1 : 0;
        }

        if (commandName == "SLTU") {
            registerData[rd] = static_cast<unsigned int>(registerData[rs1]) < static_cast<unsigned int>(registerData[rs2]) ? 1 : 0;
        }

        if (commandName == "ADDI") {
            registerData[rd] = registerData[rs1] + imm;
        }

        if (commandName == "XORI") {
            registerData[rd] = registerData[rs1] ^ imm;
        }

        if (commandName == "ORI") {
            registerData[rd] = registerData[rs1] | imm;
        }

        if (commandName == "ANDI") {
            registerData[rd] = registerData[rs1] & imm;
        }

        if (commandName == "SLLI") {
            registerData[rd] = registerData[rs1] << shamt;
        }

        if (commandName == "SRLI" || commandName == "SRAI") {
            registerData[rd] = registerData[rs1] >> shamt;
        }

        if (commandName == "SLTI") {
            registerData[rd] = registerData[rs1] < imm ? 1 : 0;
        }

        if (commandName == "SLTIU") {
            registerData[rd] = static_cast<unsigned int>(registerData[rs1]) < static_cast<unsigned int>(imm) ? 1 : 0;
        }

        if (commandName == "MUL") {
            long long result = (long long) registerData[rs1] * (long long) registerData[rs2];
            for (int bit = 32; bit < 64; bit++) {
                if ((result >> bit) & 1) {
                    result -= (1LL << bit);
                }
            }

            registerData[rd] = (int) result;
        }

        if (commandName == "MULH") {
            long long result = static_cast<long long>(registerData[rs1]) * static_cast<long long>(registerData[rs2]);
            for (int bit = 0; bit < 32; bit++) {
                if ((result >> bit) & 1) {
                    result -= (1LL << bit);
                }
            }

            for (int bit = 32; bit < 64; bit++) {
                if ((result >> bit) & 1) {
                    result += (1 << (bit - 32));
                    result -= (1LL << bit);
                }
            }

            registerData[rd] = (int) result;
        }

        if (commandName == "MULSU") {
            long long result = static_cast<long long>(registerData[rs1]) * static_cast<unsigned long long>(registerData[rs2]);
            for (int bit = 0; bit < 32; bit++) {
                if ((result >> bit) & 1) {
                    result -= (1LL << bit);
                }
            }

            for (int bit = 32; bit < 64; bit++) {
                if ((result >> bit) & 1) {
                    result += (1 << (bit - 32));
                    result -= (1LL << bit);
                }
            }

            registerData[rd] = (int) result;
        }

        if (commandName == "MULU") {
            long long result = static_cast<unsigned long long>(registerData[rs1]) * static_cast<unsigned long long>(registerData[rs2]);
            for (int bit = 0; bit < 32; bit++) {
                if ((result >> bit) & 1) {
                    result -= (1LL << bit);
                }
            }

            for (int bit = 32; bit < 64; bit++) {
                if ((result >> bit) & 1) {
                    result += (1 << (bit - 32));
                    result -= (1LL << bit);
                }
            }

            registerData[rd] = (int) result;
        }

        if (commandName == "DIV") {
            if (registerData[rs2] == 0) {
                cerr << "Division by zero\n";
                exit(0);
            } else {
                registerData[rd] = registerData[rs1] / registerData[rs2];
            }
        }

        if (commandName == "DIVU") {
            if (registerData[rs2] == 0) {
                cerr << "Division by zero\n";
                exit(0);
            } else {
                registerData[rd] =
                        static_cast<unsigned int>(registerData[rs1]) / static_cast<unsigned int>(registerData[rs2]);
            }
        }

        if (commandName == "REM") {
            if (registerData[rs2] == 0) {
                cerr << "Division by zero\n";
                exit(0);
            } else {
                registerData[rd] = registerData[rs1] % registerData[rs2];
            }
        }

        if (commandName == "REMU") {
            if (registerData[rs2] == 0) {
                cerr << "Division by zero\n";
                exit(0);
            } else {
                registerData[rd] =
                        static_cast<unsigned int>(registerData[rs1]) % static_cast<unsigned int>(registerData[rs2]);
            }
        }

        if (commandName == "BEQ") {
            if (registerData[rs1] == registerData[rs2]) {
                PC += imm;
                continue;
            }
        }

        if (commandName == "BNE") {
            if (registerData[rs1] != registerData[rs2]) {
                PC += imm;
                continue;
            }
        }

        if (commandName == "BLT") {
            if (registerData[rs1] < registerData[rs2]) {
                PC += imm;
                continue;
            }
        }

        if (commandName == "BLTU") {
            if (static_cast<unsigned int>(registerData[rs1]) < static_cast<unsigned int>(registerData[rs2])) {
                PC += imm;
                continue;
            }
        }

        if (commandName == "BGE") {
            if (registerData[rs1] >= registerData[rs2]) {
                PC += imm;
                continue;
            }
        }

        if (commandName == "BGEU") {
            if (static_cast<unsigned int>(registerData[rs1]) >= static_cast<unsigned int>(registerData[rs2])) {
                PC += imm;
                continue;
            }
        }

        if (commandName == "JAL") {
            registerData[rd] = PC + 4;
            PC += imm;
            continue;
        }

        if (commandName == "JALR") {
            registerData[rd] = PC + 4;
            PC = rs1 + imm;
            continue;
        }

        if (commandName == "LUI") {
            registerData[rd] = imm << 12;
        }

        if (commandName == "AUIPC") {
            registerData[rd] = PC + (imm << 12);
        }

        if (commandName == "LB") {
            int id = registerData[rs1] + imm;

            registerData[rd] = getData(id, true);
            deleteBits(registerData[rd], 8, 31);
        }

        if (commandName == "LH") {
            int id = registerData[rs1] + imm;

            registerData[rd] = getData(id, true);
            deleteBits(registerData[rd], 16, 31);
        }

        if (commandName == "LW") {
            int id = registerData[rs1] + imm;

            registerData[rd] = getData(id, true);
        }

        if (commandName == "LBU") {
            unsigned int id = static_cast<unsigned int>(registerData[rs1] + imm);

            registerData[rd] = getData(id, true);
            deleteBits(registerData[rd], 8, 31);
        }

        if (commandName == "LHU") {
            unsigned int id = static_cast<unsigned int>(registerData[rs1] + imm);

            registerData[rd] = getData(id, true);
            deleteBits(registerData[rd], 16, 31);
        }

        if (commandName == "SB") {
            cout << "data: " << rs1 << " " << rs2 << " " << imm << endl;

            int id = registerData[rs1] + imm;

            cout << id << " " << memoryLRU.size() << endl;

            int newData = getData(id, false);

            cout << "was\n";

            cout << rs2 << endl;

            deleteBits(newData, 0, 7);

            cout << newData << endl;

            for (int bit = 0; bit <= 7; bit++) {
                if ((registerData[rs2] >> bit) & 1) {
                    newData += (1 << bit);
                }
            }

            cout << "was\n";

            updateData(id, newData);

            cout << "was\n";
        }

        if (commandName == "SH") {
            int id = registerData[rs1] + imm;

            int newData = getData(id, false);

            deleteBits(newData, 0, 15);
            for (int bit = 0; bit <= 15; bit++) {
                if ((registerData[rs2] >> bit) & 1) {
                    newData += (1 << bit);
                }
            }

            updateData(id, newData);
        }

        if (commandName == "SW") {
            int id = registerData[rs1] + imm;

            int newData = getData(id, false);

            deleteBits(newData, 0, 31);
            for (int bit = 0; bit <= 31; bit++) {
                if ((registerData[rs2] >> bit) & 1) {
                    newData += (1 << bit);
                }
            }

            updateData(id, newData);
        }

        PC += 4;
    }

    return {LRU.getResult(), LRU.getInstructionResult(), LRU.getDataResult(), pLRU.getResult(), pLRU.getInstructionResult(), pLRU.getDataResult()};
}

int main(int argc, char* argv[]) {
//    const char *asmFile = nullptr;
//    const char *binFile = nullptr;
//
//    for (int i = 1; i < argc; i++) {
//        string arg = argv[i];
//
//        if (arg == "--asm" && i + 1 < argc) {
//            asmFile = argv[i + 1];
//        }
//
//        if (arg == "--bin" && i + 1 < argc) {
//            binFile = argv[i + 1];
//        }
//    }
//
//    if (asmFile == nullptr || binFile == nullptr) {
//        cerr << "Cannnot find asm or bin file" << endl;
//        exit(0);
//    }

    freopen("input.txt", "r", stdin);

    vector<vector<string>> commands = readCommands();

    vector<double> result = simulateCache(commands);

    printf("replacement\thit rate\thit rate (inst)\thit rate (data)\n");
    printf("        LRU\t%3.5f%%\t%3.5f%%\t%3.5f%%\n", abs(result[0]), abs(result[1]), abs(result[2]));
    printf("       pLRU\t%3.5f%%\t%3.5f%%\t%3.5f%%\n", abs(result[3]), abs(result[4]), abs(result[5]));

    vector<string> inBin = commandsToBin(commands);

    vector<vector<string>> binCommands = getBinCommands(inBin);

 //   ofstream out(binFile, ios::binary);

    for (vector<string> binCommand : binCommands) {
        for (string value : binCommand) {
            cout << char(stoi(value, nullptr, 16));
        }
    }
    return 0;
}