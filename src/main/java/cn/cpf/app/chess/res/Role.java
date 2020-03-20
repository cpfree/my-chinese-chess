package cn.cpf.app.chess.res;

import cn.cpf.app.chess.base.ArrayUtils;
import lombok.Getter;

public enum Role {
    /**
     * 以确定规则: 1. form 和 to 不相等
     */
    Boss((pieces, part, from, to) -> {
        // 一次只能走一格
        if (1 != Math.abs(to.x - from.x) + Math.abs(to.y - from.y)) {
            return false;
        }
        // 必须在 3 * 3营地
        if (to.x > 5 || to.x < 3) {
            return false;
        }
        if (part == Part.RED) {
            // && to.y < 9;
            return to.y >= 7;
        } else {
            return to.y <= 2;
        }
    }),
    Counselor((pieces, part, from, to) -> {
        // 斜着走
        if (1 != Math.abs(to.x - from.x) || 1 != Math.abs(to.y - from.y)) {
            return false;
        }
        // 必须在 3 * 3营地
        if (to.x > 5 || to.x < 3) {
            return false;
        }
        if (part == Part.RED) {
            // && to.y < 9;
            return to.y >= 7;
        } else {
            return to.y <= 2;
        }
    }), elephant((pieces, part, from, to) -> {
        int xSub = to.x - from.x;
        int ySub = to.y - from.y;
        // 斜着走2步, 象眼处无棋子
        if (2 != Math.abs(xSub) || 2 != Math.abs(ySub) || pieces[from.x + xSub / 2][from.y + ySub / 2] != null) {
            return false;
        }
        // 必须在本营地
        if (part == Part.RED) {
            return to.y >= 5;
        } else {
            return to.y <= 4;
        }
    }), car((pieces, part, from, to) -> {
        // 直走且道路畅通
        if (to.x == from.x && to.y != from.y && ArrayUtils.numberInMiddle(pieces[from.x], to.y, from.y) == 0) {
            return true;
        }
        if (to.x != from.x && to.y == from.y && ArrayUtils.numberInMiddle(pieces, to.x, to.y, from.y) == 0) {
            return true;
        }
        return false;
    }), cannon((pieces, part, from, to) -> {
        if (pieces[to.x][to.y] == null) {
            if (to.x == from.x && to.y != from.y && ArrayUtils.numberInMiddle(pieces[from.x], to.y, from.y) == 0) {
                return true;
            }
            if (to.x != from.x && to.y == from.y && ArrayUtils.numberInMiddle(pieces, to.x, to.y, from.y) == 0) {
                return true;
            }
        } else {
            if (to.x == from.x && to.y != from.y && ArrayUtils.numberInMiddle(pieces[from.x], to.y, from.y) == 1) {
                return true;
            }
            if (to.x != from.x && to.y == from.y && ArrayUtils.numberInMiddle(pieces, to.x, to.y, from.y) == 1) {
                return true;
            }
        }
        return false;
    }), horse((pieces, part, from, to) -> {
        // 斜着走2步
        int xSub = to.x - from.x;
        int ySub = to.y - from.y;
        // 为日字, 且道路通畅
        if ((Math.abs(xSub) == 2 && Math.abs(ySub) == 1) || pieces[from.x + (xSub / 2)][from.y] == null) {
            return true;
        }
        if ((Math.abs(ySub) == 2 && Math.abs(xSub) == 1) || pieces[from.x][from.y + (ySub / 2)] == null) {
            return true;
        }
        return false;
    }), soldier((pieces, part, from, to) -> {
        // 一次只能走一格
        if (1 != Math.abs(to.x - from.x) + Math.abs(to.y - from.y)) {
            return false;
        }
        if (part == Part.RED) {
            // 不能后退
            if (to.y > from.y) {
                return false;
            }
            // 过河前不能左右走
            if (from.y >= 5 && to.x != from.x) {
                return false;
            }
        } else {
            // 不能后退
            if (to.y < from.y) {
                return false;
            }
            // 过河前不能左右走
            if (from.y <= 4 && to.x != from.x) {
                return false;
            }
        }
        return true;
    });

    @Getter
    private Rule rule;

    Role(Rule rule) {
        this.rule = rule;
    }

}
