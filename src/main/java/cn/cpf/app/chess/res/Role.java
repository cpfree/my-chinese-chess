package cn.cpf.app.chess.res;

import cn.cpf.app.chess.base.ArrayUtils;
import cn.cpf.app.chess.bean.AnalysisBean;
import cn.cpf.app.chess.bean.ChessPiece;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Role {
    /**
     * 以确定规则: 1. form 和 to 不相等
     */
    Boss(new Rule() {
        @Override
        public boolean check(ChessPiece[][] pieces, Part part, Place from, Place to) {
            // 一次只能走一格
            if (1 != Math.abs(to.x - from.x) + Math.abs(to.y - from.y)) {
                return false;
            }
            // 必须在 3 * 3营地
            if (to.x > 5 || to.x < 3) {
                return false;
            }
            if (Part.RED == part) {
                // && to.y < 9;
                return to.y >= 7;
            } else {
                return to.y <= 2;
            }
        }

        @Override
        public List<Place> find(AnalysisBean analysisBean, Part part, Place place) {
            ChessPiece[][] pieces = analysisBean.chessPieces;
            int x = place.x;
            int y = place.y;
            // TODO 待优化
            List<Place> list = new ArrayList<>(4);
            // x轴移动
            Place oppoBossPlace = analysisBean.getOppoPlace(part);
            if (x == 4) {
                // 对方boss x坐标为3, 且移动后双 boss 不会对面
                if (checkPlace(pieces[3][y], part) >= 0 && !(oppoBossPlace.x == 3 && analysisBean.bossF2fAfterBossMove(part, Place.of(3, y)))) {
                    list.add(Place.of(3, y));
                }
                if (checkPlace(pieces[5][y], part) >= 0 && !(oppoBossPlace.x == 5 && analysisBean.bossF2fAfterBossMove(part, Place.of(5, y)))) {
                    list.add(Place.of(5, y));
                }
            } else {
                if (checkPlace(pieces[4][y], part) >= 0 && !(oppoBossPlace.x == 4 && analysisBean.bossF2fAfterBossMove(part, Place.of(4, y)))) {
                    list.add(Place.of(4, y));
                }
            }
            if (Part.RED == part) {
                if (y == 8) {
                    if (checkPlace(pieces[x][7], part) >= 0) {
                        list.add(Place.of(x, 7));
                    }
                    if (checkPlace(pieces[x][9], part) >= 0) {
                        list.add(Place.of(x, 9));
                    }
                } else {
                    if (checkPlace(pieces[x][8], part) >= 0) {
                        list.add(Place.of(x, 8));
                    }
                }
            } else {
                if (y == 1) {
                    if (checkPlace(pieces[x][0], part) >= 0) {
                        list.add(Place.of(x, 0));
                    }
                    if (checkPlace(pieces[x][2], part) >= 0) {
                        list.add(Place.of(x, 2));
                    }
                } else {
                    if (checkPlace(pieces[x][1], part) >= 0) {
                        list.add(Place.of(x, 1));
                    }
                }
            }
            return list;
        }

        @Override
        public int getScore(ChessPiece[][] chessPieces, Place place) {
            return 100;
        }
    }),
    Counselor(new Rule() {
        @Override
        public boolean check(ChessPiece[][] pieces, Part part, Place from, Place to) {
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
        }

        @Override
        public List<Place> find(ChessPiece[][] pieces, Part part, Place place) {
            int x = place.x;
            int yCenter = Part.RED == part ? 8 : 1;
            if (x != 4) {
                if (checkPlace(pieces[4][yCenter], part) >= 0) {
                    return Collections.singletonList(Place.of(4, yCenter));
                }
                return Collections.emptyList();
            }
            List<Place> list = new ArrayList<>(4);
            yCenter += 1;
            if (checkPlace(pieces[3][yCenter], part) >= 0) {
                list.add(Place.of(3, yCenter));
            }
            if (checkPlace(pieces[5][yCenter], part) >= 0) {
                list.add(Place.of(5, yCenter));
            }
            yCenter -= 2;
            if (checkPlace(pieces[3][yCenter], part) >= 0) {
                list.add(Place.of(3, yCenter));
            }
            if (checkPlace(pieces[5][yCenter], part) >= 0) {
                list.add(Place.of(5, yCenter));
            }
            return list;
        }

        @Override
        public int getScore(ChessPiece[][] chessPieces, Place place) {
            return 100;
        }

    }), elephant(new Rule() {
        @Override
        public boolean check(ChessPiece[][] pieces, Part part, Place from, Place to) {
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
        }

        @Override
        public List<Place> find(ChessPiece[][] pieces, Part part, Place place) {
            int x = place.x;
            int y = place.y;
            int yCenter = Part.RED == part ? 7 : 2;
            List<Place> list = new ArrayList<>(4);
            if (x == 0) {
                if (pieces[1][yCenter - 1] == null && checkPlace(pieces[2][yCenter - 2], part) >= 0) {
                    list.add(Place.of(2, yCenter - 2));
                }
                if (pieces[1][yCenter + 1] == null && checkPlace(pieces[2][yCenter + 2], part) >= 0) {
                    list.add(Place.of(2, yCenter + 2));
                }
            } else if (x == 2) {
                int yEye = (y + yCenter) / 2;
                if (pieces[1][yEye] == null && checkPlace(pieces[0][yCenter], part) >= 0) {
                    list.add(Place.of(0, yCenter));
                }
                if (pieces[3][yEye] == null && checkPlace(pieces[4][yCenter], part) >= 0) {
                    list.add(Place.of(4, yCenter));
                }
            } else if (x == 4) {
                int yEye = yCenter + 1;
                int yTmp = yCenter + 2;
                if (pieces[3][yEye] == null && checkPlace(pieces[2][yTmp], part) >= 0) {
                    list.add(Place.of(2, yTmp));
                }
                if (pieces[5][yEye] == null && checkPlace(pieces[6][yTmp], part) >= 0) {
                    list.add(Place.of(6, yTmp));
                }
                yEye = yCenter - 1;
                yTmp = yCenter - 2;
                if (pieces[3][yEye] == null && checkPlace(pieces[2][yTmp], part) >= 0) {
                    list.add(Place.of(2, yTmp));
                }
                if (pieces[5][yEye] == null && checkPlace(pieces[6][yTmp], part) >= 0) {
                    list.add(Place.of(6, yTmp));
                }
            } else if (x == 6) {
                int yEye = (y + yCenter) / 2;
                if (pieces[5][yEye] == null && checkPlace(pieces[4][yCenter], part) >= 0) {
                    list.add(Place.of(4, yCenter));
                }
                if (pieces[7][yEye] == null && checkPlace(pieces[8][yCenter], part) >= 0) {
                    list.add(Place.of(8, yCenter));
                }
            } else if (x == 8) {
                if (pieces[7][yCenter - 1] == null && checkPlace(pieces[6][yCenter - 2], part) >= 0) {
                    list.add(Place.of(6, yCenter - 2));
                }
                if (pieces[7][yCenter + 1] == null && checkPlace(pieces[6][yCenter + 2], part) >= 0) {
                    list.add(Place.of(6, yCenter + 2));
                }
            }
            return list;
        }
    }), car(new Rule() {
        @Override
        public boolean check(ChessPiece[][] pieces, Part part, Place from, Place to) {
            // 直走且道路畅通
            if (to.x == from.x && to.y != from.y) {
                return ArrayUtils.nullInMiddle(pieces[from.x], to.y, from.y);
            }
            if (to.x != from.x && to.y == from.y) {
                return ArrayUtils.nullInMiddle(pieces, to.y, to.x, from.x);
            }
            return false;
        }

        @Override
        public List<Place> find(ChessPiece[][] pieces, Part part, Place place) {
            int xInit = place.x;
            int yInit = place.y;
            List<Place> list = new ArrayList<>();
            for (int x = xInit - 1; x >= 0 ; x--) {
                ChessPiece chessPiece = pieces[x][yInit];
                if (chessPiece == null) {
                    list.add(Place.of(x, yInit));
                    continue;
                }
                if (chessPiece.part != part) {
                    list.add(Place.of(x, yInit));
                }
                break;
            }
            for (int x = xInit + 1; x < 9 ; x++) {
                ChessPiece chessPiece = pieces[x][yInit];
                if (chessPiece == null) {
                    list.add(Place.of(x, yInit));
                    continue;
                }
                if (chessPiece.part != part) {
                    list.add(Place.of(x, yInit));
                }
                break;
            }
            for (int y = yInit - 1; y >= 0 ; y--) {
                ChessPiece chessPiece = pieces[xInit][y];
                if (chessPiece == null) {
                    list.add(Place.of(xInit, y));
                    continue;
                }
                if (chessPiece.part != part) {
                    list.add(Place.of(xInit, y));
                }
                break;
            }
            for (int y = yInit + 1; y <= 9 ; y++) {
                ChessPiece chessPiece = pieces[xInit][y];
                if (chessPiece == null) {
                    list.add(Place.of(xInit, y));
                    continue;
                }
                if (chessPiece.part != part) {
                    list.add(Place.of(xInit, y));
                }
                break;
            }
            return list;
        }
    }), cannon(new Rule() {
        @Override
        public boolean check(ChessPiece[][] pieces, Part part, Place from, Place to) {
            if (pieces[to.x][to.y] == null) {
                if (to.x == from.x && to.y != from.y) {
                    return ArrayUtils.nullInMiddle(pieces[from.x], to.y, from.y);
                }
                if (to.x != from.x && to.y == from.y) {
                    return ArrayUtils.nullInMiddle(pieces, to.y, to.x, from.x);
                }
            } else {
                if (to.x == from.x && to.y != from.y) {
                    return ArrayUtils.oneInMiddle(pieces[from.x], to.y, from.y);
                }
                if (to.x != from.x && to.y == from.y) {
                    return ArrayUtils.oneInMiddle(pieces, to.y, to.x, from.x);
                }
            }
            return false;
        }

        @Override
        public List<Place> find(ChessPiece[][] pieces, Part part, Place place) {
            int xInit = place.x;
            int yInit = place.y;
            List<Place> list = new ArrayList<>();
            boolean kong = false;
            for (int x = xInit - 1; x >= 0 ; x--) {
                ChessPiece chessPiece = pieces[x][yInit];
                if (kong) {
                    if (chessPiece == null) {
                        continue;
                    }
                    if (chessPiece.part != part) {
                        list.add(Place.of(x, yInit));
                    }
                    break;
                } else {
                    if (chessPiece == null) {
                        list.add(Place.of(x, yInit));
                        continue;
                    }
                    kong = true;
                    continue;
                }
            }
            kong = false;
            for (int x = xInit + 1; x < 9 ; x++) {
                ChessPiece chessPiece = pieces[x][yInit];
                if (kong) {
                    if (chessPiece == null) {
                        continue;
                    }
                    if (chessPiece.part != part) {
                        list.add(Place.of(x, yInit));
                    }
                    break;
                } else {
                    if (chessPiece == null) {
                        list.add(Place.of(x, yInit));
                        continue;
                    }
                    kong = true;
                    continue;
                }
            }
            kong = false;
            for (int y = yInit - 1; y >= 0 ; y--) {
                ChessPiece chessPiece = pieces[xInit][y];
                if (kong) {
                    if (chessPiece == null) {
                        continue;
                    }
                    if (chessPiece.part != part) {
                        list.add(Place.of(xInit, y));
                    }
                    break;
                } else {
                    if (chessPiece == null) {
                        list.add(Place.of(xInit, y));
                        continue;
                    }
                    kong = true;
                    continue;
                }
            }
            kong = false;
            for (int y = yInit + 1; y < 10 ; y++) {
                ChessPiece chessPiece = pieces[xInit][y];
                if (kong) {
                    if (chessPiece == null) {
                        continue;
                    }
                    if (chessPiece.part != part) {
                        list.add(Place.of(xInit, y));
                    }
                    break;
                } else {
                    if (chessPiece == null) {
                        list.add(Place.of(xInit, y));
                        continue;
                    }
                    kong = true;
                    continue;
                }
            }
            return list;
        }
    }), horse(new Rule() {
        @Override
        public boolean check(ChessPiece[][] pieces, Part part, Place from, Place to) {
            // 斜着走2步
            int xSub = to.x - from.x;
            int ySub = to.y - from.y;
            // 为日字, 且道路通畅
            if ((Math.abs(xSub) == 2 && Math.abs(ySub) == 1)) {
                return pieces[from.x + (xSub / 2)][from.y] == null;
            }
            if ((Math.abs(ySub) == 2 && Math.abs(xSub) == 1)) {
                return pieces[from.x][from.y + (ySub / 2)] == null;
            }
            return false;
        }

        @Override
        public List<Place> find(ChessPiece[][] pieces, Part part, Place place) {
            int xInit = place.x;
            int yInit = place.y;
            List<Place> list = new ArrayList<>(8);
            if (xInit > 1 && pieces[xInit - 1][yInit] == null) {
                if (yInit > 0) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit - 2, yInit - 1));
                }
                if (yInit < 9) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit - 2, yInit + 1));
                }
            }
            if (xInit < 7 && pieces[xInit + 1][yInit] == null) {
                if (yInit > 0) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit + 2, yInit - 1));
                }
                if (yInit < 9) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit + 2, yInit + 1));
                }
            }
            if (yInit > 1 && pieces[xInit][yInit - 1] == null) {
                if (xInit > 0) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit - 1, yInit - 2));
                }
                if (xInit < 8) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit + 1, yInit - 2));
                }
            }
            if (yInit < 8 && pieces[xInit][yInit + 1] == null) {
                if (xInit > 0) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit - 1, yInit + 2));
                }
                if (xInit < 8) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit + 1, yInit + 2));
                }
            }
            return list;
        }
    }), soldier(new Rule() {
        @Override
        public boolean check(ChessPiece[][] pieces, Part part, Place from, Place to) {
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
        }

        @Override
        public List<Place> find(ChessPiece[][] pieces, Part part, Place place) {
            int xInit = place.x;
            int yInit = place.y;
            List<Place> list = new ArrayList<>(3);
            if (part == Part.RED) {
                if (yInit < 5) {
                    // 可以左右移动
                    if (xInit > 0) {
                        addPlaceIntoList(pieces, part, list, Place.of(xInit - 1, yInit));
                    }
                    if (xInit < 8) {
                        addPlaceIntoList(pieces, part, list, Place.of(xInit + 1, yInit));
                    }
                }
                if (yInit > 0) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit, yInit - 1));
                }
            } else {
                if (yInit > 4) {
                    // 可以左右移动
                    if (xInit > 0) {
                        addPlaceIntoList(pieces, part, list, Place.of(xInit - 1, yInit));
                    }
                    if (xInit < 8) {
                        addPlaceIntoList(pieces, part, list, Place.of(xInit + 1, yInit));
                    }
                }
                if (yInit < 9) {
                    addPlaceIntoList(pieces, part, list, Place.of(xInit, yInit + 1));
                }
            }
            return list;
        }
    });

    private final Rule rule;

    Role(Rule rule) {
        this.rule = rule;
    }

    public boolean check(AnalysisBean analysisBean, Part part, Place from, Place to) {
        if (this == Role.Boss) {
            if (analysisBean.bossF2fAfterBossMove(part, to)) {
                return false;
            }
            return rule.check(analysisBean.chessPieces, part, from, to);
        } else {
            if (to.x != from.x && analysisBean.bossF2fAfterLeave(from)) {
                return false;
            }
            return rule.check(analysisBean.chessPieces, part, from, to);
        }
    }


    public List<Place> find(AnalysisBean analysisBean, Part part, Place place) {
        if (this == Role.Boss) {
            return rule.find(analysisBean, part, place);
        } else {
            return rule.find(analysisBean.chessPieces, part, place);
//            if (analysisBean.bossF2fAfterLeave(place)) {
//            } else {
//                return Collections.emptyList();
//            }
        }
    }

}
