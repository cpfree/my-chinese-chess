package cn.cpf.app.chess.bean;

public enum Part {
	/**
	 * 红方势力
	 */
	RED,
	
	/**
	 * 黑方势力
	 */
	BLACK;
	
	/**
	 * 返回相反的势力
	 * @param part
	 * @return
	 */
	public static Part getOpposite(Part part){
		if (part == RED){
			return BLACK;
		} else {
			return RED;
		}
	}
}
