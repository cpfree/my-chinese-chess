package cn.cpf.app.chess.domain;

import cn.cpf.app.chess.bean.Part;

public enum  Piece {

    BlackBoss(Part.BLACK, Role.Boss, "1.jpg"),
    BlackCounselor(Part.BLACK, Role.Counselor, "2.jpg"),
    BlackElephant(Part.BLACK, Role.elephant, "3.jpg"),
    BlackCar(Part.BLACK, Role.car, "4.jpg"),
    BlackHorse(Part.BLACK, Role.horse, "5.jpg"),
    BlackCannon(Part.BLACK, Role.cannon, "6.jpg"),
    BlackSoldier(Part.BLACK, Role.soldier, "7.jpg"),
    RedBoss(Part.RED, Role.Boss, "8.jpg"),
    RedCounselor(Part.RED, Role.Counselor, "9.jpg"),
    RedElephant(Part.RED, Role.elephant, "10.jpg"),
    RedCar(Part.RED, Role.car, "11.jpg"),
    RedHorse(Part.RED, Role.horse, "12.jpg"),
    RedCannon(Part.RED, Role.cannon, "13.jpg"),
    RedSoldier(Part.RED, Role.soldier, "14.jpg")
    ;

    /**
     * 红方或黑方
     */
    private Part part;

    private Role role;

    private String image;

    Piece(Part part, Role role, String image) {
        this.part = part;
        this.role = role;
        this.image = image;
    }
}
