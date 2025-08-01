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
#include <string>
#include <algorithm>

using namespace std;

string mulTwoNumbers(string a, string b) {
    reverse(a.begin(), a.end());
    reverse(b.begin(), b.end());

    vector<int> res(a.size() + b.size() + 1, 0);

    for (int i = 0; i < a.size(); i++) {
        for (int j = 0; j < b.size(); j++) {
            res[i + j] += (a[i] - '0') * (b[j] - '0');
        }
    }

    for (int i = 0; i < res.size() - 1; i++) {
        if (res[i] >= 10) {
            res[i + 1] += res[i] / 10;
            res[i] %= 10;
        }
    }

    string answer = "";

    for (int i = 0; i < res.size(); i++) {
        answer += char(res[i] + '0');
    }

    reverse(answer.begin(), answer.end());

    int cntZero = 0;

    for (int i = 0; i < answer.size(); i++) {
        if (answer[i] != '0') {
            break;
        }

        cntZero++;
    }

    return answer.substr(cntZero, answer.size() - cntZero);
}

string pow(string a, long long p) {
    string result = "1";

    while (p--) {
        result = mulTwoNumbers(result, a);
    }

    return result;
}

long long getModularNumber(long long number, int A, int B) {
    long long newNumber = 0;

    for (int bit = 0; bit < A + B; bit++) {
        if ((number >> bit) & 1) {
            newNumber += (1 << bit);
        }
    }

    return newNumber;
}

long long getFromHex(string number, int A, int B) {
    long long integerNumber = 0;
    long long currentPow = 1;

    for (long long hexIndex = (long long) number.size() - 1; hexIndex >= 2; hexIndex--) {
        long long hexValue;

        if (isdigit(number[hexIndex])) {
            hexValue = number[hexIndex] - '0';
        } else {
            hexValue = tolower(number[hexIndex]) - 'a' + 10;
        }

        integerNumber += currentPow * hexValue;

        currentPow *= 16;
    }

    return getModularNumber(integerNumber, A, B);
}

bool checkForZero(long long number, int A, int B) {
    bool zeroFlag = true;

    for (int bit = 0; bit < A + B; bit++) {
        if ((number >> bit) & 1) {
            zeroFlag = false;
        }
    }

    return zeroFlag;
}

long long getNumberSign(long long number, int A, int B) {
    if (checkForZero(number, A, B)) {
        return 0;
    } else if ((number >> (A + B - 1)) & 1) {
        return -1;
    } else {
        return 1;
    }
}

long long towardZero(long long number, int A, int B) {
    return (long long) (number >> B);
}

long long towardNearestEven(long long number, int A, int B) {
    if ((number >> B) & 1) {
        if ((number >> (B - 1)) & 1) {
            return (long long) ((number >> B) + 1);
        } else {
            return (long long) (number >> B);
        }
    } else {
        int countBits = 0;

        for (int bit = 0; bit < B; bit++) {
            if ((number >> bit) & 1) {
                countBits++;
            }
        }

        if (countBits > 1) {
            return (long long) ((number >> B) + 1);
        } else {
            return (long long) (number >> B);
        }
    }
}

long long towardPosInfinity(long long number, int A, int B, int sign) {
    if (sign <= 0) {
        return (long long) (number >> B);
    } else {
        bool zeroFractionPart = true;

        for (int bit = 0; bit < B; bit++) {
            if ((number >> bit) & 1) {
                zeroFractionPart = false;
            }
        }

        if (zeroFractionPart) {
            return (long long) (number >> B);
        } else {
            number += (1 << B);
            return (long long) (number >> B);
        }
    }
}

long long towardNegInfinity(long long number, int A, int B, int sign) {
    if (sign >= 0) {
        return (long long) (number >> B);
    } else {
        bool zeroFractionPart = true;

        for (int bit = 0; bit < B; bit++) {
            if ((number >> bit) & 1) {
                zeroFractionPart = false;
            }
        }

        if (zeroFractionPart) {
            return (long long) (number >> B);
        } else {
            number -= (1 << B);
            return (long long) (number >> B);
        }
    }
}

long long roundNumber(long long number, int A, int B, int roundType, int sign) {
    switch (roundType) {
        case 0:
            return towardZero(number, A, B);
            break;
        case 1:
            return towardNearestEven(number, A, B);
            break;
        case 2:
            return towardPosInfinity(number, A, B, sign);
            break;
        case 3:
            return towardNegInfinity(number, A, B, sign);
    }
}

long long getFractionalPart(long long number, int bitCount, int sign) {
    long long fractionalPart = 0;

    for (int bit = 0; bit < bitCount; bit++) {
        if (sign == 1 && (number >> bit) & 1) {
            fractionalPart += (1 << bit);
        }
        if (sign == -1 && !((number >> bit) & 1)) {
            fractionalPart += (1 << bit);
        }
    }

    if (sign == -1) {
        fractionalPart = (fractionalPart + 1) % (1LL << bitCount);
    }

    return fractionalPart;
}

long long getDecimalPart(long long number, int A, int B) {
    long long decimalPart = 0;

    for (int bit = B; bit < A + B; bit++) {
        if ((number >> bit) & 1) {
            decimalPart += (1LL << (bit - B));
        }
    }

    bool notZero = false;

    for (int bit = 0; bit < B; bit++) {
        if ((number >> bit) & 1) {
            notZero = true;
        }
    }

    if ((number >> (A + B - 1)) & 1) {
        decimalPart -= (1LL << A);
        decimalPart += (notZero);
    }

    return (long long) decimalPart;
}

string stringPlusOne(string value) {
    long long c = 1;

    for (long long index = (long long) value.size() - 1; index >= 0; index--) {
        if (c == 0) {
            break;
        }

        long long newValue = (value[index] - '0') + c;
        if (newValue >= 10) {
            newValue %= 10;
        } else {
            c = 0;
        }

        value[index] = char(newValue + '0');
    }

    if (c == 1) {
        value = '1' + value;
    }

    return value;
}

void getNewFractionalPart(string &fractionalResult, long long &decimalPart, bool up) {
    if (up) {
        fractionalResult = fractionalResult.substr(0, 3);
        string newFractionalResult = stringPlusOne(fractionalResult);
        if (newFractionalResult.size() != fractionalResult.size()) {
            decimalPart++;
            fractionalResult = newFractionalResult.substr(1, 3);
        } else {
            fractionalResult = newFractionalResult;
        }
    } else {
        fractionalResult = fractionalResult.substr(0, 3);
    }
}

void printNumber(long long number, int A, int B, int roundType) {
    int sign = getNumberSign(number, A, B);

    long long decimalPart = getDecimalPart(number, A, B);
    long long fractionalPart = getFractionalPart(number, B, sign);

    string fractionalResult = mulTwoNumbers(pow("5", B), to_string(fractionalPart));

    while (fractionalResult.size() < B) {
        fractionalResult = '0' + fractionalResult;
    }

    if (fractionalResult.size() > 3) {
        switch (roundType) {
            case 0:
                fractionalResult = fractionalResult.substr(0, 3);
                break;
            case 1:
            {
                string toComp = fractionalResult.substr(3, fractionalResult.size() - 3);

                string middle = "5";
                for (long long step = 0; step < toComp.size() - 1; step++) {
                    middle += '0';
                }

                char who = '=';

                for (long long index = 0; index < (long long) toComp.size(); index++) {
                    if (toComp[index] != middle[index]) {
                        if (toComp[index] < middle[index]) {
                            who = '<';
                        } else {
                            who = '>';
                        }
                        break;
                    }
                }

                fractionalResult = fractionalResult.substr(0, 3);

                getNewFractionalPart(fractionalResult, decimalPart, ((who == '=' && (fractionalResult[2] - '0') % 2) || who == '>'));
                break;
            }
            case 2:
            {
                bool notZero = false;

                for (long long index = 3; index < (long long) fractionalResult.size(); index++) {
                    if (fractionalResult[index] != '0') {
                        notZero = true;
                    }
                }

                if (notZero && sign == 1) {
                    getNewFractionalPart(fractionalResult, decimalPart, true);
                }

                fractionalResult = fractionalResult.substr(0, 3);

                break;
            }
            case 3:
            {
                bool notZero = false;

                for (long long index = 3; index < (long long) fractionalResult.size(); index++) {
                    if (fractionalResult[index] != '0') {
                        notZero = true;
                    }
                }

                fractionalResult = fractionalResult.substr(0, 3);

                if (notZero && sign == -1) {
                    getNewFractionalPart(fractionalResult, decimalPart, true);
                }
                break;
            }
        }
    }

    while (fractionalResult.size() < 3) fractionalResult += '0';

    if (sign == -1 && decimalPart == 0) {
        cout << '-';
    }

    cout << decimalPart << "." << fractionalResult << endl;
}

long long getInvertNumber(long long number, int A, int B) {
    long long newNumber = 0;

    for (int bit = 0; bit < A + B; bit++) {
        if (!((number >> bit) & 1)) {
            newNumber += (1LL << bit);
        }
    }

    newNumber = getModularNumber(newNumber + 1, A, B);

    return newNumber;
}

long long sumOfNumbers(long long firstnumber, long long secondNumber, int A, int B) {
    return getModularNumber(firstnumber + secondNumber, A, B);
}

long long multiplyOfNumbers(long long firstNumber, long long secondNumber, int A, int B, int roundType) {
    long long fn, sn;

    int resultSign = getNumberSign(firstNumber, A, B) * getNumberSign(secondNumber, A, B);

    if (getNumberSign(firstNumber, A, B) == -1) {
        fn = firstNumber - (1LL << (A + B));
    } else {
        fn = firstNumber;
    }

    if (getNumberSign(secondNumber, A, B) == -1) {
        sn = secondNumber - (1LL << (A + B));
    } else {
        sn = secondNumber;
    }

    long long result = (long long) fn * (long long) sn;

    result = roundNumber(result, A, B, roundType, resultSign);

    if (result < 0) {
        result += (1 << (A + B));
    }

    result = getModularNumber(result, A, B);

    return (long long) result;
}

long long dividingOfNumbers(long long firstNumber, long long secondNumber, int A, int B, int roundType) {
    long long fn, sn;

    long long resultSign = getNumberSign(firstNumber, A, B) * getNumberSign(secondNumber, A, B);

    if (getNumberSign(firstNumber, A, B) == -1) {
        fn = getInvertNumber(firstNumber, A, B);
    } else {
        fn = firstNumber;
    }

    if (getNumberSign(secondNumber, A, B) == -1) {
        sn = getInvertNumber(secondNumber, A, B);
    } else {
        sn = secondNumber;
    }

    if (checkForZero(sn, A, B)) {
        cout << "div_by_0\n";
        exit(0);
    }

    long long result = (long long) fn * (1LL << B);

    long long divPart = result / sn;
    long long modPart = (result - divPart * sn);

    switch (roundType) {
        case 0:
            break;
        case 1:
            if (modPart * 2 == sn) {
                if (abs(divPart) % 2) {
                    if (resultSign == 1) {
                        divPart++;
                    } else {
                        divPart--;
                    }
                }
            } else {
                if (modPart * 2 > sn) {
                    divPart++;
                }
            }
            break;
        case 2:
            if (modPart != 0 && resultSign >= 0) {
                divPart++;
            }
            break;
        case 3:
            if (modPart != 0 && resultSign < 0) {
                divPart++;
            }
            break;

    }

    divPart = getModularNumber(divPart, A, B);

    if ((divPart >> (A + B - 1) & 1)) {
        divPart -= (1 << (A + B - 1));
    }

    if (resultSign == -1) {
        divPart = getInvertNumber(divPart, A, B);
    }

    return (long long) divPart;
}

int main(int argc, char* argv[]) {
    if (argc != 4 && argc != 6) {
        cout << "Error: incorrect number of arguments";
        return 0;
    }

    string strA = "", strB = "";
    bool wasPoint = false;

    string AB(argv[1]), strRound(argv[2]);

    for (long long strIndex = 0; strIndex < AB.size(); strIndex++) {
        if (AB[strIndex] == '.') {
            wasPoint = true;
        } else if (!wasPoint) {
            strA += AB[strIndex];
        } else {
            strB += AB[strIndex];
        }
    }

    int A = stoi(strA), B = stoi(strB);
    int roundType = stoi(strRound);

    if (argc == 4) {
        string strN(argv[3]);

        long long number = getFromHex(strN, A, B);

        printNumber(number, A, B, roundType);
    } else {
        string strF(argv[3]), strS(argv[5]);

        long long firstNumber = getFromHex(strF, A, B), secondNumber = getFromHex(strS, A, B);
        string operationType(argv[4]);

        printNumber(firstNumber, A, B, roundType);
        printNumber(secondNumber, A, B, roundType);

        if (operationType == "+") {
            printNumber(sumOfNumbers(firstNumber, secondNumber, A, B), A, B, roundType);
        }

        if (operationType == "-") {
            printNumber(sumOfNumbers(firstNumber, getInvertNumber(secondNumber, A, B), A, B), A, B, roundType);
        }

        if (operationType == "*") {
            printNumber(multiplyOfNumbers(firstNumber, secondNumber, A, B, roundType), A, B, roundType);
        }

        if (operationType == "/") {
            printNumber(dividingOfNumbers(firstNumber, secondNumber, A, B, roundType), A, B, roundType);
        }
    }

    return 0;
}
