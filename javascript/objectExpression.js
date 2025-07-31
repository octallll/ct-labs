function Const(value) {
    this.value = value;
}

Const.prototype.toString = function () {
    return this.value.toString();
};

Const.prototype.diff = function () {
    return ZERO;
};

Const.prototype.evaluate = function () {
    return this.value;
};

Const.prototype.prefix = function () {
    return this.value.toString();
};

Const.prototype.postfix = function() {
    return this.value.toString();
};

const ZERO = new Const(0);
const ONE = new Const(1);
const THREE = new Const(3);

function Variable(name) {
    this.name = name;
}

Variable.prototype.toString = function () {
    return this.name;
};

Variable.prototype.prefix = function () {
    return this.name;
};

Variable.prototype.postfix = function() {
    return this.name;
};

const variables = {'x': 0, 'y': 1, 'z': 2, 't': 3};

Variable.prototype.diff = function (name) {
    return name === this.name ? ONE : ZERO;
};

Variable.prototype.evaluate = function (...args) {
    return args[variables[this.name]];
};

const operationProto = {
    toString: function () {
        return this.expressions.join(' ') + ' ' + this.getOperationSymbol();
    },
    evaluate: function (...args) {
        return this.calculate(...this.expressions.map(func => func.evaluate(...args)));
    },
    prefix: function () {
        return '(' + this.getOperationSymbol() + ' ' + this.expressions.map(x => x.prefix()).join(' ') + ')';
    },
    postfix : function() {
        return '(' + this.expressions.map(x => x.postfix()).join(' ') + ' ' + this.getOperationSymbol() + ')';
    }
}

function createOperation(calculateFunc, getSymbol, diffFunc) {
    function CreateOperation(...args) {
        this.expressions = args;
    }

    CreateOperation.prototype = Object.create(operationProto);
    CreateOperation.prototype.calculate = calculateFunc;
    CreateOperation.prototype.getOperationSymbol = getSymbol;
    CreateOperation.prototype.diff = function(name) {
        return diffFunc(this.expressions, this.expressions.map(x => x.diff(name)));
    }

    return CreateOperation;
}

const Add = createOperation(
    (x, y) => x + y,
    () => '+',
    (expressions, diffExpressions) => new Add(diffExpressions[0], diffExpressions[1])
)

const Subtract = createOperation(
    (x, y) => x - y,
    () => '-',
    (expressions, diffExpressions) => new Subtract(diffExpressions[0], diffExpressions[1])
)

const Multiply = createOperation(
    (x, y) => x * y,
    () => '*',
    (expressions, diffExpressions) => new Add(new Multiply(expressions[0], diffExpressions[1]), new Multiply(expressions[1], diffExpressions[0]))
)

const Divide = createOperation(
    (x, y) => x / y,
    () => '/',
    (expressions, diffExpressions) => new Divide(new Subtract(new Multiply(diffExpressions[0], expressions[1]), new Multiply(expressions[0], diffExpressions[1])), new Multiply(expressions[1], expressions[1]))
)

const Negate = createOperation(
    (x) => -x,
    () => 'negate',
    (expressions, diffExpressions) => new Negate(diffExpressions[0])
)

function createNormal(n) {
    return createOperation(
        (...args) => Math.exp(-args.map(x => (x * x) / 2).reduce((last, current) => last + current, 0)) / Math.pow((2 * Math.PI), n / 2),
        () => "normal" + n,
        (expressions, diffExpressions) => new Multiply(new Negate(expressions.reduce((last, current, i) => new Add(last, new Multiply(current, diffExpressions[i])), ZERO)), new operations["normal" + n][1](...expressions))
    )
}

const Normal1 = createNormal(1);
const Normal2 = createNormal(2);
const Normal3 = createNormal(3);
const Normal4 = createNormal(4);
const Normal5 = createNormal(5);

function createPoly(n) {
    return createOperation(
        (...args) => args.slice(1).map((x, i) => Math.pow(args[0], i) * x).reduce((last, current) => last + current, 0),
        () => "poly" + n,
        (expressions, diffExpressions) => {
            let powers = [ONE];
            let diffPowers = [ZERO];

            for (let i = 1; i < n; i++) {
                powers.push(new Multiply(expressions[0], powers[i - 1]));
                diffPowers.push(new Multiply(new Multiply(new Const(i), powers[i - 1]), diffExpressions[0]))
            }

            return expressions.splice(1).reduce((last, current, i) => new Add(last, new Add(new Multiply(current, diffPowers[i]), new Multiply(diffExpressions[i + 1], powers[i]))), ZERO);
        }
    );
}

const Poly1 = createPoly(1);
const Poly2 = createPoly(2);
const Poly3 = createPoly(3);
const Poly4 = createPoly(4);
const Poly5 = createPoly(5);

const SumCb = createOperation(
    (...args) => args.map(x => x * x * x).reduce((last, current) => last + current, 0),
    () => "sumCb",
    (expressions, diffExpressions) => expressions.reduce((last, current, i) => new Add(last, new Multiply(new Multiply(THREE, new Multiply(current, current)), diffExpressions[i])), ZERO)
)

const Rmc = createOperation(
    (...args) => Math.cbrt(args.map(x => x * x * x).reduce((last, current) => last + current, 0) / args.length),
    () => "rmc",
    (expressions, diffExpressions) => new Multiply(new Divide(new Divide(ONE, new Multiply(new Rmc(...expressions), new Rmc(...expressions))), THREE), new Divide(expressions.reduce((last, current, i) => new Add(last, new Multiply(new Multiply(THREE, new Multiply(current, current)), diffExpressions[i])), ZERO), new Const(expressions.length)))
)

const CbMax = createOperation(
    (...args) => (args[0] * args[0] * args[0] / args.map(x => x * x * x).reduce((l, c) => l + c, 0)),
    () => "cbMax",
    function (expressions,  diffExpressions) {
        let sumCubes = ZERO;
        let diffSumCubes = ZERO;

        for (let i = 0; i < expressions.length; i++) {
            sumCubes = new Add(sumCubes, new Multiply(expressions[i], new Multiply(expressions[i], expressions[i])));
            diffSumCubes = new Add(diffSumCubes, new Multiply(new Multiply(THREE, new Multiply(expressions[i], expressions[i])) ,diffExpressions[i]));
        }

        let x = new Multiply(expressions[0], new Multiply(expressions[0], expressions[0]));
        let dx = new Multiply(new Multiply(THREE, new Multiply(expressions[0], expressions[0])), diffExpressions[0]);

        return new Divide(new Subtract(new Multiply(dx, sumCubes), new Multiply(x, diffSumCubes)) ,new Multiply(sumCubes, sumCubes));
    }
)

const operations = {
    'normal1': [1, Normal1],
    'normal2': [2, Normal2],
    'normal3': [3, Normal3],
    'normal4': [4, Normal4],
    'normal5': [5, Normal5],
    'poly1': [1, Poly1],
    'poly2': [2, Poly2],
    'poly3': [3, Poly3],
    'poly4': [4, Poly4],
    'poly5': [5, Poly5],
    'negate': [1, Negate],
    '+': [2, Add],
    '-': [2, Subtract],
    '*': [2, Multiply],
    '/': [2, Divide],
    'sumCb' : [-1, SumCb],
    'rmc' : [-1, Rmc],
    'cbMax' : [-1, CbMax]
}

function parse(expression) {
    let splitted = expression.split(" ");

    let expressions = [];

    for (const current of splitted) {
        if (current.length === 0) {
            continue;
        }

        if (current in operations) {
            expressions.push(new operations[current][1](...expressions.splice(-operations[current][0])));
        } else if (current in variables) {
            expressions.push(new Variable(current));
        } else {
            expressions.push(new Const(parseInt(current)));
        }
    }

    return expressions[0];
}

const brackets = ['(', ')'];
const openToCloseBrackets = {'(' : ')'};

function splitExpression(expression) {
    return expression.split(" ").filter(x => x.length !== 0).flatMap(current => {
        const expressions = [];
        let expr = "";

        function push() {
            if (expr.length !== 0) {
                expressions.push(expr);
                expr = "";
            }
        }

        for (let symbol of current) {
            if (brackets.includes(symbol)) {
                push();
                expressions.push(symbol);
            } else {
                expr += symbol;
            }
        }
        push();

        return expressions;
    });
}

function UnexpectedOperandException(name) {
    function UnexpectedOperandException(message, ...args) {
        this.message = (args.length === 0 ? message : message + " at position " + args[0]);
        this.name = name;
    }

    UnexpectedOperandException.prototype = Object.create(Error.prototype);
    UnexpectedOperandException.prototype.constructor = UnexpectedOperandException;
    return UnexpectedOperandException;
}

function UnexpectedSymbolException(name) {
    function UnexpectedSymbolException(message, ...args) {
        this.message = (args.length === 0 ? message : message + " at position " + args[0]);
        this.name = name;
    }

    UnexpectedSymbolException.prototype = Object.create(Error.prototype);
    UnexpectedSymbolException.prototype.constructor = UnexpectedSymbolException;
    return UnexpectedSymbolException;
}

function UnknownOperationException(name) {
    function UnknownOperationException(message, ...args) {
        this.message = (args.length === 0 ? message : message + " at position " + args[0]);
        this.name = name;
    }

    UnknownOperationException.prototype = Object.create(Error.prototype);
    UnknownOperationException.prototype.constructor = UnknownOperationException;
    return UnknownOperationException;
}

const UnknownOperation = UnknownOperationException("UnknownOperationException");
const UnexpectedSymbol = UnexpectedSymbolException("UnexpectedSymbolException");
const UnexpectedOperand = UnexpectedOperandException("UnexpectedOperandException");

function parsePrefixOrPostfix(splitted, isPrefix) {
    let position = 0;

    function parseOperand() {
        if (splitted[position] in openToCloseBrackets) {
            let open = splitted[position++];
            let operands = []

            while (true) {
                operands.push(parseOperand());

                if (splitted[position] === ')') {
                    break;
                }
            }

            let operationSymbol;

            if (isPrefix) {
                operationSymbol = operands[0];
                operands.shift();
            } else {
                operationSymbol = operands[operands.length - 1];
                operands.pop();
            }

            if (!(operationSymbol in operations)) {
                throw new UnknownOperation(operationSymbol, position);
            }

            if (operations[operationSymbol][0] > 0 && operands.length !== operations[operationSymbol][0]) {
                throw new UnexpectedOperand(operands[operands.length - 1], position);
            }

            for (let operand of operands) {
                if (operand in operations) {
                    throw new UnexpectedSymbol(operand, position);
                }
            }

            if (splitted[position++] !== openToCloseBrackets[open]) {
                throw new UnexpectedSymbol("Expected " + openToCloseBrackets[open] + ", actual" + splitted[position - 1], position);
            }

            return new operations[operationSymbol][1](...operands);
        } else if (splitted[position] in variables) {
            return new Variable(splitted[position++]);
        } else if (!isNaN(splitted[position])) {
            return new Const(parseInt(splitted[position++]));
        } else if (splitted[position] in operations) {
            return splitted[position++];
        } else {
            throw new UnexpectedOperand(splitted[position], position);
        }
    }

    let result = parseOperand();

    if (position !== splitted.length) {
        throw new UnexpectedOperand(splitted[position], position);
    }

    return result;
}

function parsePrefix(expression) {
    return parsePrefixOrPostfix(splitExpression(expression), true);
}

function parsePostfix(expression) {
    return parsePrefixOrPostfix(splitExpression(expression), false);
}