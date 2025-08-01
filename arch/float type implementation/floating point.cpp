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
#include <string>
#include <cassert>

using namespace std;

struct Consts {
    int EXP_SIZE, MANT_SIZE, NUMBER_SIZE;

    Consts() {}

    Consts(int EXP_SIZE, int MANT_SIZE, int NUMBER_SIZE) {
        this->EXP_SIZE = EXP_SIZE;
        this->MANT_SIZE = MANT_SIZE;
        this->NUMBER_SIZE = NUMBER_SIZE;
    }
};

struct FloatPointNumber {
    int sign, exp, mant;

    FloatPointNumber(int sign, int exp, int mant) {
        this->sign = sign, this->exp = exp, this->mant = mant;
    }

    FloatPointNumber(string number, Consts consts) {
        string binStrNumber = "";

        for (int numIndex = 2; numIndex < number.size(); numIndex++) {
            int hexValue = (isdigit(number[numIndex]) ? number[numIndex] - '0' : tolower(number[numIndex]) - 'a' + 10);

            string binView = "";

            while (hexValue > 0) {
                binView = char(hexValue % 2 + '0') + binView;
                hexValue /= 2;
            }

            while (binView.size() < 4) binView = '0' + binView;

            binStrNumber += binView;
        }

        if (binStrNumber.size() > consts.NUMBER_SIZE) binStrNumber = binStrNumber.substr(binStrNumber.size() - consts.NUMBER_SIZE, consts.NUMBER_SIZE);

        while (binStrNumber.size() < consts.NUMBER_SIZE) binStrNumber = '0' + binStrNumber;

        char strSign = binStrNumber[0];
        string strExp = binStrNumber.substr(1, consts.EXP_SIZE);
        string strMant = binStrNumber.substr(1 + consts.EXP_SIZE, consts.MANT_SIZE);

        if (strSign == '0') {
            sign = 1;
        } else {
            sign = -1;
        }

        exp = stoi(strExp, nullptr, 2);

        mant = stoi(strMant, nullptr, 2);
    }

    string getBin(int value) {
        string result = "";

        while (value > 0) {
            result = char(value % 2 + '0') + result;
            value /= 2;
        }

        return result;
    }

    string getHexFromBin(string binStr) {
        while (binStr.size() % 4) binStr = binStr + '0';

        string result;

        for (int index = 0; index < binStr.size(); index += 4) {
            int currentPow = 8;

            int intHexValue = 0;

            for (int inIndex = 0; inIndex < 4; inIndex++) {
                if (binStr[index + inIndex] == '1') {
                    intHexValue += currentPow;
                }

                currentPow /= 2;
            }

            if (intHexValue < 10) {
                result += char(intHexValue + '0');
            } else {
                result += char(intHexValue - 10 + 'A');
            }
        }

        return result;
    }

    string getMant(Consts consts) {
        string strMant = getBin(mant);

        while (strMant.size() < consts.MANT_SIZE) strMant = '0' + strMant;

        string result = getHexFromBin(strMant);

        for (char &x : result) x = tolower(x);

        return result;
    }

    string getExp(Consts consts) {
        int expValue = exp - ((1 << (consts.EXP_SIZE - 1)) - 1);

        string resultExp = "p";

        if (expValue >= 0) resultExp += '+';

        resultExp += to_string(expValue);

        return resultExp;
    }

    int getSign() {
        return sign;
    }

    string getHex(Consts consts) {
        string binaryView = "";

        if (sign == 1) {
            binaryView += '0';
        } else {
            binaryView += '1';
        }

        string strExp = getBin(exp);

        while (strExp.size() < consts.EXP_SIZE) {
            strExp = '0' + strExp;
        }

        binaryView += strExp;

        string strMant = getBin(mant);

        while (strMant.size() < consts.MANT_SIZE) {
            strMant = '0' + strMant;
        }

        binaryView += strMant;

        return getHexFromBin(binaryView);
    }
};

FloatPointNumber getInvertNumber(FloatPointNumber number) {
    int newSign = (number.sign == 1 ? -1 : 1);

    return FloatPointNumber(newSign, number.exp, number.mant);
}

bool checkInf(FloatPointNumber number, Consts consts) {
    return (number.exp == (1 << consts.EXP_SIZE) - 1 && number.mant == 0);
}

FloatPointNumber getInf(int sign, Consts consts) {
    return FloatPointNumber(sign, (1 << consts.EXP_SIZE) - 1, 0);
}

bool checkNaN(FloatPointNumber number, Consts consts) {
    return (number.exp == (1 << consts.EXP_SIZE) - 1 && number.mant > 0);
}

FloatPointNumber NaNToQuiet(FloatPointNumber number, Consts consts) {
    if ((number.mant >> (consts.MANT_SIZE - 1)) & 1) {
        return number;
    } else {
        number.mant += (1 << (consts.MANT_SIZE - 1));
        return number;
    }
}

FloatPointNumber getNaN(Consts consts) {
    return FloatPointNumber(-1, (1 << consts.EXP_SIZE) - 1, (1 << (consts.MANT_SIZE - 1)));
}

bool checkZero(FloatPointNumber number) {
    return number.exp == 0 && number.mant == 0;
}

FloatPointNumber getZero(int sign) {
    return FloatPointNumber(sign, 0, 0);
}

void printNumber(FloatPointNumber number, Consts consts) {
    if (checkInf(number, consts)) {
        if (number.sign == -1) {
            cout << '-';
        }

        cout << "inf";
    } else if (checkZero(number)) {
        if (number.sign == -1) {
            cout << '-';
        }

        cout << "0x0." << number.getMant(consts) << "p+0";
    } else if (checkNaN(number, consts)) {
        cout << "nan";
    } else {
        if (number.getSign() == -1) {
            cout << '-';
        }

        cout << "0x1." << number.getMant(consts) << number.getExp(consts);
    }

    cout << " ";
    cout << "0x" << number.getHex(consts);
}

int getRoundNumber(long long divPart, long long modPart, long long divider, int roundType, int sign, Consts consts) {
    if (roundType == 0) {
        return divPart;
    }

    if (roundType == 1) {
        if (modPart * 2 > divider) {
            return divPart + 1;
        } else if (modPart * 2 < divider) {
            return divPart;
        } else {
            if (divPart % 2) {
                return divPart + 1;
            } else {
                return divPart;
            }
        }
    }

    if (roundType == 2) {
        if (modPart == 0) {
            return divPart;
        } else {
            if (sign == 1) {
                return divPart + 1;
            } else {
                return divPart;
            }
        }
    }

    if (roundType == 3) {
        if (modPart == 0) {
            return divPart;
        } else {
            if (sign == 1) {
                return divPart;
            } else {
                return divPart + 1;
            }
        }
    }

    return 0LL;
}

long long cut(long long number, int remain) {
    for (int bit = 0; bit < 64; bit++) {
        if (bit < remain) continue;
        if ((number >> bit) & 1) {
            number -= (1LL << bit);
        }
    }

    return number;
}

FloatPointNumber mulTwoNumbers(FloatPointNumber firstNumber, FloatPointNumber secondNumber, int roundType, Consts consts) {
    if (checkNaN(firstNumber, consts)) {
        return NaNToQuiet(firstNumber, consts);
    }

    if (checkNaN(secondNumber, consts)) {
        return NaNToQuiet(secondNumber, consts);
    }

    if (checkInf(firstNumber, consts)) {
        if (checkZero(secondNumber)) {
            return getNaN(consts);
        } else {
            return getInf(firstNumber.sign * secondNumber.sign, consts);
        }
    }

    if (checkInf(secondNumber, consts)) {
        if (checkZero(firstNumber)) {
            return getNaN(consts);
        } else {
            return getInf(firstNumber.sign * secondNumber.sign, consts);
        }
    }

    if (checkZero(firstNumber) || checkZero(secondNumber)) {
        return getZero(firstNumber.sign * secondNumber.sign);
    }

    int newSign = firstNumber.sign * secondNumber.sign;

    int newExp = firstNumber.exp + secondNumber.exp - ((1 << (consts.EXP_SIZE - 1)) - 1);

    long long newMantWithoutRound = ((long long) firstNumber.mant + (1 << consts.MANT_SIZE)) * ((long long) secondNumber.mant + (1 << consts.MANT_SIZE));

    int valueBeforePoint = 0;

    for (int bit = 2 * consts.MANT_SIZE; bit < 64; bit++) {
        if ((newMantWithoutRound >> bit) & 1) {
            valueBeforePoint += (1 << (bit - 2 * consts.MANT_SIZE));
        }
    }

    long long divPart, modPart, divider;

    if (valueBeforePoint > 1) {
        newExp++;
        newMantWithoutRound = cut(newMantWithoutRound, 2 * consts.MANT_SIZE + 1);

        divider = (1LL << (consts.MANT_SIZE + 1));
        divPart = newMantWithoutRound / divider, modPart = newMantWithoutRound % divider;
    } else {
        newMantWithoutRound = cut(newMantWithoutRound, 2 * consts.MANT_SIZE);

        divider = (1LL << consts.MANT_SIZE);
        divPart = newMantWithoutRound / divider, modPart = newMantWithoutRound % divider;
    }

    int newMant = getRoundNumber(divPart, modPart, divider, roundType, newSign, consts);

    if (newExp > (1 << consts.EXP_SIZE) - 1) {
        return getInf(newSign, consts);
    }

    if (newExp < 0) {
        return getZero(newSign);
    }

    return FloatPointNumber(newSign, newExp, newMant);
}

FloatPointNumber divTwoNumbers(FloatPointNumber firstNumber, FloatPointNumber secondNumber, int roundType, Consts consts) {
    if (checkNaN(firstNumber, consts)) {
        return NaNToQuiet(firstNumber, consts);
    }

    if (checkNaN(secondNumber, consts)) {
        return NaNToQuiet(secondNumber, consts);
    }

    if (checkZero(secondNumber)) {
        if (checkZero(firstNumber)) {
            return getNaN(consts);
        } else {
            return getInf(firstNumber.sign * secondNumber.sign, consts);
        }
    }

    if (checkZero(firstNumber)) {
        return getZero(firstNumber.sign * secondNumber.sign);
    }

    if (checkInf(firstNumber, consts) && checkInf(secondNumber, consts)) {
        return getNaN(consts);
    }

    if (checkInf(firstNumber, consts)) {
        return getInf(firstNumber.sign * secondNumber.sign, consts);
    }

    if (checkInf(secondNumber, consts)) {
        return getZero(firstNumber.sign * secondNumber.sign);
    }

    int newSign = firstNumber.sign * secondNumber.sign;

    int newExp = firstNumber.exp - secondNumber.exp + ((1 << (consts.EXP_SIZE - 1)) - 1);

    long long divPart, modPart, divider = ((long long) secondNumber.mant + (1 << consts.MANT_SIZE));

    if (firstNumber.mant < secondNumber.mant) {
        newExp--;
        divPart = (2LL * ((long long) firstNumber.mant + (1 << consts.MANT_SIZE)) * (1LL << consts.MANT_SIZE)) / divider;
        modPart = (2LL * ((long long) firstNumber.mant + (1 << consts.MANT_SIZE)) * (1LL << consts.MANT_SIZE)) % divider;
    } else {
        divPart = ((long long) (firstNumber.mant + (1 << consts.MANT_SIZE)) * (1LL << consts.MANT_SIZE)) / divider;
        modPart = ((long long) (firstNumber.mant + (1 << consts.MANT_SIZE)) * (1LL << consts.MANT_SIZE)) % divider;
    }

    divPart %= (1 << consts.MANT_SIZE);

    int newMant = getRoundNumber(divPart, modPart, divider, roundType, newSign, consts);

    if (newExp > (1 << consts.EXP_SIZE) - 1) {
        return getInf(newSign, consts);
    }

    if (newExp < 0) {
        return getZero(newSign);
    }

    return FloatPointNumber(newSign, newExp, newMant);
}

FloatPointNumber sumOrSubTwoNumbers(FloatPointNumber firstNumber, FloatPointNumber secondNumber, int roundType, string type, Consts consts) {
    if (checkNaN(firstNumber, consts)) {
        return NaNToQuiet(firstNumber, consts);
    }

    if (checkNaN(secondNumber, consts)) {
        return NaNToQuiet(secondNumber, consts);
    }

    if (checkInf(firstNumber, consts) && checkInf(secondNumber, consts)) {
        if (type == "sum") {
            if (firstNumber.sign * secondNumber.sign == 1) {
                return firstNumber;
            } else {
                return getNaN(consts);
            }
        } else {
            if (firstNumber.sign * secondNumber.sign == 1) {
                return getNaN(consts);
            } else {
                return firstNumber;
            }
        }
    }

    if (checkInf(firstNumber, consts)) {
        return firstNumber;
    }

    if (checkInf(secondNumber, consts)) {
        return getInf(secondNumber.sign * (type == "sum" ? 1 : -1), consts);
    }

    if (checkZero(firstNumber) && checkZero(secondNumber)) {
        if ((firstNumber.sign == -1 && secondNumber.sign == -1 && type == "sum") && (firstNumber.sign == -1 && secondNumber.sign == 1 && type == "sub")) {
            return getZero(-1);
        } else {
            return getZero(1);
        }
    }

    if (checkZero(firstNumber)) {
        return FloatPointNumber(secondNumber.sign * (type == "sum" ? 1 : -1), secondNumber.exp, secondNumber.mant);
    }

    if (checkZero(secondNumber)) {
        return firstNumber;
    }

    int newSign;

    bool wasSwap = false;

    if (firstNumber.exp > secondNumber.exp) {
        swap(firstNumber, secondNumber);
        wasSwap = true;
    }

    int expDelta = secondNumber.exp - firstNumber.exp;

    int newExp = firstNumber.exp;

    if (expDelta < 25) {
        long long newMantWithoutRound;

        long long firstMant = ((long long) firstNumber.mant + (1LL << consts.MANT_SIZE)), secondMant = (1LL << expDelta) * (long long) (secondNumber.mant + (1LL << consts.MANT_SIZE));

        if (type == "sum") {
            if (firstNumber.sign == 1 && secondNumber.sign == 1) {
                newMantWithoutRound = firstMant + secondMant;
                newSign = 1;
            }

            if (firstNumber.sign == -1 && secondNumber.sign == -1) {
                newMantWithoutRound = firstMant + secondMant;
                newSign = -1;
            }

            if (firstNumber.sign == -1 && secondNumber.sign == 1) {
                newMantWithoutRound = abs(firstMant - secondMant);

                if (firstMant != secondMant) newSign = (secondMant >= firstMant ? 1 : -1);
                else newSign = 1;
            }

            if (firstNumber.sign == 1 && secondNumber.sign == -1) {
                newMantWithoutRound = abs(firstMant - secondMant);

                if (firstMant != secondMant) newSign = (firstMant >= secondMant ? 1 : -1);
                else newSign = 1;
            }
        } else {
            if (firstNumber.sign == 1 && secondNumber.sign == -1) {
                newMantWithoutRound = firstMant + secondMant;
                newSign = (wasSwap ? -1 : 1);
            }

            if (firstNumber.sign == -1 && secondNumber.sign == 1) {
                newMantWithoutRound = firstMant + secondMant;
                newSign = -1 * (wasSwap ? -1 : 1);
            }

            if (firstNumber.sign == -1 && secondNumber.sign == -1) {
                newMantWithoutRound = abs(firstMant - secondMant);

                if (firstMant != secondMant) newSign = (wasSwap ? -1 : 1) * (secondMant >= firstMant ? 1 : -1);
                else newSign = 1;
            }

            if (firstNumber.sign == 1 && secondNumber.sign == 1) {
                newMantWithoutRound = abs(firstMant - secondMant);

                if (firstMant != secondMant) newSign = (wasSwap ? -1 : 1) * (firstMant >= secondMant ? 1 : -1);
                else newSign = 1;
            }
        }

        int firstBit = -1;

        for (int bit = 0; bit < 64; bit++) {
            if ((newMantWithoutRound >> bit) & 1) {
                firstBit = bit;
            }
        }

        if (firstBit == -1) {
            return FloatPointNumber(newSign, 0, 0);
        }

        int more = expDelta;

        if (firstBit >= consts.MANT_SIZE) {
            more = firstBit - consts.MANT_SIZE;
            newExp += firstBit - consts.MANT_SIZE;
            newMantWithoutRound -= (1LL << firstBit);
        }

        long long divider = (1LL << more);
        long long divPart = newMantWithoutRound / divider, modPart = newMantWithoutRound % divider;

        int newMant = getRoundNumber(divPart, modPart, divider, roundType, newSign, consts);
        if (newExp <= (1 << consts.EXP_SIZE) - 1) {
            return FloatPointNumber(newSign, newExp, newMant);
        } else {
            return getInf(newSign, consts);
        }
    } else {
        newExp = secondNumber.exp;

        if (wasSwap) {
            newSign = secondNumber.sign;
        } else {
            newSign = secondNumber.sign * (type == "sum" ? 1 : -1);
        }

        int newMant = secondNumber.mant;

        if (firstNumber.sign * secondNumber.sign * (type == "sum" ? 1 : -1) == -1) {
            newMant--;
        }

        if (firstNumber.mant > 0) {
            if (roundType == 2) {
                if (newSign == 1) {
                    newMant++;
                }
            }

            if (roundType == 3) {
                if (newSign == -1) {
                    newMant++;
                }
            }
        }

        return FloatPointNumber(newSign, newExp, newMant);
    }
}

int main(int argc, char* argv[]) {
    if (argc != 4 && argc != 6) {
        cerr << "Error: incorrect number of arguments";
        exit(0);
    }

    string halfOrFloat(argv[1]);
    int roundType = stoi(string(argv[2]));

    Consts consts;

    if (halfOrFloat == "h") {
        consts = Consts(5, 10, 16);
    } else {
        consts = Consts(8, 23, 32);
    }

    if (argc == 4) {
        string num(argv[3]);
        FloatPointNumber number(num, consts);
        printNumber(number, consts);
    } else {
        string num1(argv[3]), num2(argv[5]);

        FloatPointNumber firstNumber(num1, consts), secondNumber(num2, consts);

        string operationType = string(argv[4]);

        if (operationType == "+") {
            printNumber(sumOrSubTwoNumbers(firstNumber, secondNumber, roundType, "sum", consts), consts);
        }
        if (operationType == "-") {
            printNumber(sumOrSubTwoNumbers(firstNumber, secondNumber, roundType, "sub", consts), consts);
        }
        if (operationType == "*") {
            printNumber(mulTwoNumbers(firstNumber, secondNumber, roundType, consts), consts);
        }
        if (operationType == "/") {
            printNumber(divTwoNumbers(firstNumber, secondNumber, roundType, consts), consts);
        }
    }
    return 0;
}
