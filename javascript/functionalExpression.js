const operation = (f) => (...args) => (...variables) => {
    let calculated = [];

    for (const current of args) {
        calculated.push(current(...variables))
    }

    return f(...calculated);
}

const add = operation((x, y) => x + y);
const subtract = operation((x, y) => x - y);
const multiply = operation((x, y) => x * y);
const divide = operation((x, y) => x / y);

const negate = operation((x) => -x);

const cnst = (x) => () => x;

const variables = {'x': 0, 'y': 1, 'z': 2, 't': 3};

const variable = (name) => (...args) => args[variables[name]];

function compareOperation(func, ...args) {
    let result = 1;

    for (let i = 1; i < args.length; i++) {
        if (!func(args[i - 1], args[i])) {
            result = 0;
        }
    }

    return result;
}

const lessEq2 = operation((...args) => compareOperation((x, y) => x <= y, ...args));
const greaterEq5 = operation((...args) => compareOperation((x, y) => x >= y, ...args));
const less3 = operation((...args) => compareOperation((x, y) => x < y, ...args));
const greater4 = operation((...args) => compareOperation((x, y) => x > y, ...args));;

const phi = cnst((1 + Math.sqrt(5)) / 2);
const tau = cnst(2 * Math.PI);

const constants = {'phi': phi, 'tau': tau};

const operations = {
    'negate' : [negate, 1],
    '+' : [add, 2],
    '-' : [subtract, 2],
    '*' : [multiply, 2],
    '/' : [divide, 2],
    'lessEq2' : [lessEq2, 2],
    'less3' : [less3, 3],
    'greater4' : [greater4, 4],
    'greaterEq5' : [greaterEq5, 5]
}

function makeOperation(func, numberOfArguments, stack) {
    let args = [];

    for (let step = 0; step < numberOfArguments; step++) {
        args.push(stack.pop());
    }

    args.reverse();

    return func(...args);
}

function parse(expression) {
    let splitted = expression.split(" ");

    let expressions = []

    for (const current of splitted) {
        if (current.length === 0) {
            continue;
        }

        if (current in operations) {
            expressions.push(makeOperation(...operations[current], expressions));
        } else if (current in variables) {
            expressions.push(variable(current));
        } else if (current in constants) {
            expressions.push(constants[current]);
        } else {
            expressions.push(cnst(parseInt(current)));
        }
    }

    return expressions[0];
}

function main() {
    let str_expression = "x x * 2 x * - 1 +"

    let expression = parse(str_expression)

    for (let i = 0; i <= 10; i++) {
        println("Result for " + i + ": " + expression(i));
    }
}