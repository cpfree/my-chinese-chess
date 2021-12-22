package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.inter.MyList;
import cn.cpf.app.chess.inter.Rule;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.util.ArrayUtils;
import cn.cpf.app.chess.util.ListPool;

public enum Role {
    /**
     * 以确定规则: 1. form 和 to 不相等
     */
    BOSS(new Rule() {
        @Override
        public boolean check(Piece[][] pieces, Part part, Place from, Place to) {
            // 一次只能走一格
            if (1 != Math.abs(to.x - from.x) + Math.abs(to.y - from.y)) {
                return false;
            }
            // 必须在 3 * 3营地
            if (to.x > 5 || to.x < 3) {
                return false;
            }
            // 此处默认 Place 的 y 在 [0,9] 之间。
            if (Part.RED == part) {
                return to.y >= 7;
            } else {
                return to.y <= 2;
            }
        }

        @Override
        public MyList<Place> find(Piece[][] pieces, Part part, Place place) {
            AnalysisBean analysisBean = new AnalysisBean(pieces);
            final int x = place.x;
            final int y = place.y;
            final MyList<Place> list = ListPool.localPool().getAPlaceList(4);
            // x轴移动
            Place oppoBossPlace = analysisBean.getOppoBossPlace(part);
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
    }),
    COUNSELOR(new Rule() {
        @Override
        public boolean check(Piece[][] pieces, Part part, Place from, Place to) {
            // 斜着走
            if (1 != Math.abs(to.x - from.x) || 1 != Math.abs(to.y - from.y)) {
                return false;
            }
            // 必须在 3 * 3营地
            if (to.x > 5 || to.x < 3) {
                return false;
            }
            if (part == Part.RED) {
                return to.y >= 7;
            } else {
                return to.y <= 2;
            }
        }

        @Override
        public MyList<Place> find(Piece[][] pieces, Part part, Place place) {
            final int x = place.x;
            int yCenter = Part.RED == part ? 8 : 1;
            if (x != 4) {
                if (checkPlace(pieces[4][yCenter], part) >= 0) {
                    final MyList<Place> places = ListPool.localPool().getAPlaceList(1);
                    places.add(Place.of(4, yCenter));
                    return places;
                }
                return ListPool.getEmptyList();
            }
            final MyList<Place> list = ListPool.localPool().getAPlaceList(4);
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

    }), ELEPHANT(new Rule() {
        @Override
        public boolean check(Piece[][] pieces, Part part, Place from, Place to) {
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
        public MyList<Place> find(Piece[][] pieces, Part part, Place place) {
            final int x = place.x;
            final int y = place.y;
            final int yCenter = Part.RED == part ? 7 : 2;
            final MyList<Place> list = ListPool.localPool().getAPlaceList(4);
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
    }), CAR(new Rule() {
        @Override
        public boolean check(Piece[][] pieces, Part part, Place from, Place to) {
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
        @SuppressWarnings("java:S135")
        public MyList<Place> find(Piece[][] pieces, Part part, Place place) {
            final int xInit = place.x;
            final int yInit = place.y;
            final MyList<Place> list = ListPool.localPool().getAPlaceList(17);
            for (int x = xInit - 1; x >= 0; x--) {
                Piece chessPiece = pieces[x][yInit];
                if (chessPiece == null) {
                    list.add(Place.of(x, yInit));
                    continue;
                }
                if (chessPiece.part != part) {
                    list.add(Place.of(x, yInit));
                }
                break;
            }
            for (int x = xInit + 1; x < 9; x++) {
                Piece chessPiece = pieces[x][yInit];
                if (chessPiece == null) {
                    list.add(Place.of(x, yInit));
                    continue;
                }
                if (chessPiece.part != part) {
                    list.add(Place.of(x, yInit));
                }
                break;
            }
            for (int y = yInit - 1; y >= 0; y--) {
                Piece chessPiece = pieces[xInit][y];
                if (chessPiece == null) {
                    list.add(Place.of(xInit, y));
                    continue;
                }
                if (chessPiece.part != part) {
                    list.add(Place.of(xInit, y));
                }
                break;
            }
            for (int y = yInit + 1; y <= 9; y++) {
                Piece chessPiece = pieces[xInit][y];
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
    }), CANNON(new Rule() {
        @Override
        public boolean check(Piece[][] pieces, Part part, Place from, Place to) {
            if (from == to) {
                return false;
            }
            if (pieces[to.x][to.y] == null) {
                if (to.x == from.x) {
                    return ArrayUtils.nullInMiddle(pieces[from.x], to.y, from.y);
                }
                if (to.y == from.y) {
                    return ArrayUtils.nullInMiddle(pieces, to.y, to.x, from.x);
                }
            } else {
                if (to.x == from.x) {
                    return ArrayUtils.oneInMiddle(pieces[from.x], to.y, from.y);
                }
                if (to.y == from.y) {
                    return ArrayUtils.oneInMiddle(pieces, to.y, to.x, from.x);
                }
            }
            return false;
        }

        @Override
        @SuppressWarnings({"java:S135", "java:S3626"})
        public MyList<Place> find(Piece[][] pieces, Part part, Place place) {
            final int xInit = place.x;
            final int yInit = place.y;
            final MyList<Place> list = ListPool.localPool().getAPlaceList(17);
            boolean kong = false;
            for (int x = xInit - 1; x >= 0; x--) {
                Piece chessPiece = pieces[x][yInit];
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
                }
            }
            kong = false;
            for (int x = xInit + 1; x < 9; x++) {
                Piece chessPiece = pieces[x][yInit];
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
                }
            }
            kong = false;
            for (int y = yInit - 1; y >= 0; y--) {
                Piece chessPiece = pieces[xInit][y];
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
                }
            }
            kong = false;
            for (int y = yInit + 1; y < 10; y++) {
                Piece chessPiece = pieces[xInit][y];
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
                }
            }
            return list;
        }
    }), HORSE(new Rule() {
        @Override
        public boolean check(Piece[][] pieces, Part part, Place from, Place to) {
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
        public MyList<Place> find(Piece[][] pieces, Part part, Place place) {
            final int xInit = place.x;
            final int yInit = place.y;
            final MyList<Place> list = ListPool.localPool().getAPlaceList(8);
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
    }), SOLDIER(new Rule() {
        @Override
        public boolean check(Piece[][] pieces, Part part, Place from, Place to) {
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
                return from.y < 5 || to.x == from.x;
            } else {
                // 不能后退
                if (to.y < from.y) {
                    return false;
                }
                // 过河前不能左右走
                return from.y > 4 || to.x == from.x;
            }
        }

        @Override
        public MyList<Place> find(Piece[][] pieces, Part part, Place place) {
            final int xInit = place.x;
            final int yInit = place.y;
            final MyList<Place> list = ListPool.localPool().getAPlaceList(3);
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

    public final Rule rule;

    Role(Rule rule) {
        this.rule = rule;
    }

    public MyList<Place> find(AnalysisBean analysisBean, Part curPart, Place from) {
        if (this == Role.BOSS) {
            return rule.find(analysisBean.pieces, curPart, from);
        } else {
            MyList<Place> places = rule.find(analysisBean.pieces, curPart, from);
            if (analysisBean.isBossF2FAndWithOnlyThePlaceInMiddle(from)) {
                return analysisBean.filterPlace(places);
            }
            return places;
        }
    }

    public MyList<Place> find(Piece[][] piece, Part curPart, Place from) {
        final AnalysisBean analysisBean = new AnalysisBean(piece);
        return find(analysisBean, curPart, from);
    }

}
