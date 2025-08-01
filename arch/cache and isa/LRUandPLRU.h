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

#include <vector>
#include <algorithm>
#include <climits>

using namespace std;

enum DataType {
    Instruction,
    Data
};

struct CacheConsts {
    const int MEM_SIZE = 262144;
    const int ADDR_LEN = 18;
    const int CACHE_WAY = 4;
    const int CACHE_TAG_LEN = 9;
    const int CACHE_INDEX_LEN = 3;
    const int CACHE_OFFSET_LEN = 6;
    const int CACHE_SIZE = 2048;
    const int CACHE_LINE_SIZE = 64;
    const int CACHE_LINE_COUNT = 32;
    const int CACHE_SETS = 8;
} Consts;

struct CacheLine {
    int tag;
    int latestUse;
    int tagUseBit;
    bool dirty;

    vector<int> data;

    CacheLine() {
        tag = -1;
        tagUseBit = 0;
        latestUse = -1;
        dirty = false;

        data.resize(Consts.CACHE_LINE_SIZE);
    }
};

int getTag(int id) {
    int tag = 0;

    for (int bit = Consts.CACHE_OFFSET_LEN + Consts.CACHE_INDEX_LEN; bit < Consts.CACHE_OFFSET_LEN + Consts.CACHE_INDEX_LEN + Consts.CACHE_TAG_LEN; bit++) {
        if ((id >> bit) & 1) {
            tag += (1 << (bit - Consts.CACHE_OFFSET_LEN - Consts.CACHE_INDEX_LEN));
        }
    }

    return tag;
}

int getIndex(int id) {
    int index = 0;

    for (int bit = Consts.CACHE_OFFSET_LEN; bit < Consts.CACHE_OFFSET_LEN + Consts.CACHE_INDEX_LEN; bit++) {
        if ((id >> bit) & 1) {
            index += (1 << (bit - Consts.CACHE_OFFSET_LEN));
        }
    }

    return index;
}

int getOffset(int id) {
    int offset = 0;

    for (int bit = 0; bit < Consts.CACHE_OFFSET_LEN; bit++) {
        if ((id >> bit) & 1) {
            offset += (1 << bit);
        }
    }

    return offset;
}

class Cache {
public:
    int queriesCount, dataQueriesCount, instructionQueriesCount;
    int inCount, dataInCount, instructionInCount;

    vector<vector<CacheLine>> lines;

    bool updateData(int id, int newData) {
        int tag = getTag(id);
        int index = getIndex(id);
        int offset = getOffset(id);

        for (CacheLine &line : lines[index]) {
            if (line.tag == tag) {
                line.dirty = true;
                line.data[offset] = newData;

                return true;
            }
        }

        return false;
    }

    int getData(int id) {
        int tag = getTag(id);
        int index = getIndex(id);
        int offset = getOffset(id);

        for (CacheLine line : lines[index]) {
            if (line.tag == tag) {
                return line.data[offset];
            }
        }

        return INT_MAX;
    }

    double getResult() {
        return (double) inCount / queriesCount * 100;
    }

    double getDataResult() {
        return (double) dataInCount / dataQueriesCount * 100;
    }

    double getInstructionResult() {
        return (double) instructionInCount / instructionQueriesCount * 100;
    }
};

class LRUCache : public Cache {
    int timer;
public:
    LRUCache() {
        timer = 0;

        queriesCount = dataQueriesCount = instructionQueriesCount = 0;

        inCount = dataInCount = instructionInCount = 0;

        lines.resize(Consts.CACHE_SETS, vector<CacheLine>(Consts.CACHE_WAY));
    }

    bool memoryGet(int id, DataType type) {
        timer++;

        queriesCount++;

        if (type == Data) {
            dataQueriesCount++;
        }

        if (type == Instruction) {
            instructionQueriesCount++;
        }

        int tag = getTag(id);
        int index = getIndex(id);

        bool lineFound = false;

        for (CacheLine line: lines[index]) {
            if (line.tag == tag) {
                lineFound = true;
                line.latestUse = timer;
            }
        }

        if (lineFound) {
            inCount++;

            if (type == Instruction) {
                instructionInCount++;
            }

            if (type == Data) {
                dataInCount++;
            }
        }

        return lineFound;
    }

    void insertData(int id, vector<int> data, vector<int> &mem) {
        int tag = getTag(id);
        int index = getIndex(id);

        int memStart = id - id % Consts.CACHE_LINE_SIZE;

        int latestUseValue = INT_MAX;

        for (CacheLine line: lines[index]) {
            latestUseValue = min(latestUseValue, line.latestUse);
        }

        for (CacheLine &line: lines[index]) {
            if (latestUseValue == line.latestUse) {
                if (line.dirty) {
                    for (int memIndex = memStart; memIndex < memStart + Consts.CACHE_LINE_SIZE; memIndex++) {
                        mem[memIndex] = line.data[memIndex - memStart];
                    }
                }

                line.tag = tag;
                line.latestUse = timer;
                line.data = data;
                line.dirty = false;

                break;
            }
        }
    }
};

class pLRUCache : public Cache {
public:
    pLRUCache() {
        lines.resize(Consts.CACHE_SETS, vector<CacheLine> (Consts.CACHE_WAY));

        queriesCount = dataQueriesCount = instructionQueriesCount = 0;

        inCount = dataInCount = instructionInCount = 0;
    }

    bool memoryGet(int id, DataType type) {
        queriesCount++;

        if (type == Data) {
            dataQueriesCount++;
        }

        if (type == Instruction) {
            instructionQueriesCount++;
        }

        int tag = getTag(id);
        int index = getIndex(id);

        bool lineFound = false;

        for (CacheLine line : lines[index]) {
            if (line.tag == tag) {
                lineFound = true;
            }
        }

        if (lineFound) {
            inCount++;

            if (type == Instruction) {
                instructionInCount++;
            }

            if (type == Data) {
                dataInCount++;
            }
        }

        return lineFound;
    }

    void insertData(int id, vector<int> data, vector<int> &mem) {
        int tag = getTag(id);
        int index = getIndex(id);

        int memStart = id - id % Consts.CACHE_LINE_SIZE;

        for (CacheLine &line : lines[index]) {
            if (line.tagUseBit == 0) {
                if (line.dirty) {
                    for (int memIndex = memStart; memIndex < memStart + Consts.CACHE_LINE_SIZE; memIndex++) {
                        mem[memIndex] = line.data[memIndex - memStart];
                    }
                }

                line.tag = tag;
                line.tagUseBit = 1;
                line.data = data;
                line.dirty = false;

                break;
            }
        }

        int newLinesCount = 0;

        for (CacheLine line : lines[index]) {
            if (line.tagUseBit == 1) {
                newLinesCount++;
            }
        }

        if (newLinesCount == Consts.CACHE_LINE_COUNT) {
            for (CacheLine &line : lines[index]) {
                if (line.tag != tag) {
                    line.tagUseBit = 0;
                }
            }
        }
    }
};