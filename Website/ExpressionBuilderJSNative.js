var closest = Number.MAX_VALUE;
var ops = ["+", "-", "*", "/"];

function buildExpression(list, target){
    if(list.length > 7){
        ui = confirm("You have entered " + list.length + 
            " numbers. Beware that this may cause the tab to freeze for a long period or crash. Press 'OK' to continue, or 'Cancel' to abort");
        if(!ui){
            return "Enter fewer numbers this time!     :)";
        }
    }
    var res = buildExpressionHelper(list, target);
    return res == null ? " No solution found. Try " + closest + " as a target instead. " : res.slice(1, res.length - 1) + " = " + target; 
}

function buildExpressionHelper(list, target){
    var last = list[list.length - 1];
    closest = Math.abs(target - last) < Math.abs(target - closest) ? last : closest;
    if(last == target){
        return target.toString();
    }
    for(var i = 0; i < list.length - 1; i++){
        for(var j = i + 1; j < list.length; j++){
            for(var a = 0; a < ops.length; a++){
                 var res = getRes(list, target, i, j, ops[a]);
                if(res != null){
                     return res;
                }
            }
        }
    }
    return null;
}

function getRes(list, target, i, j, op){
    var first = list[i], second = list[j], newList = list.slice(0), res;
    newList.splice(i, 1);
    newList.splice(j - 1, 1);
    switch(op){
        case "+": 
            newList.push(first + second);
            res = buildExpressionHelper(newList, target);
            if(res != null){
                return expandExpression(first, second, res, "+", first + second)
            }
            newList.pop();
            break;
        case "-":
            newList.push(Math.abs(first - second));
            res = buildExpressionHelper(newList, target);
            if(res != null){
                return expandExpression(Math.max(first, second), Math.min(first, second), res, "-", Math.abs(first - second))
            }
            newList.pop();
            break;
        case "*":
            newList.push(first * second);
            res = buildExpressionHelper(newList, target);
            if(res != null){
                return expandExpression(first, second, res, "*", first * second)
            }
            newList.pop();
            break;
        case "/":
            if(first % second == 0 && second != 0){
                newList.push((first / second) << 0)
                res = buildExpressionHelper(newList, target);
                if(res != null){
                    return expandExpression(first, second, res, "/", (first / second) << 0)
                }
                newList.pop();
                break;
            }
            if(second % first == 0 && first != 0){
                newList.push((second / first) << 0)
                res = buildExpressionHelper(newList, target);
                if(res != null){
                    return expandExpression(second, first, res, "/", (second / first) << 0)
                }
                newList.pop();
                break;
            }
        default:
            return null;
    }
}

function expandExpression(first, second, curr, op, numToReplace){
    return curr.replace(numToReplace.toString(), "(" + first + " " + op + " " + second + ")");
}
