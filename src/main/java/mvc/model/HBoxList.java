package mvc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ArrayList를 상속받아 구현한 hBoxList
 * 
 * @author In-seong Hwang
 * @since 2018.11.11 * 
 * 
 * @param <E>
 */
public class HBoxList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;
	
	public HBoxList() {
		super();
	}
	/**
	 * <pre>
	 * List 를 가져와 ABoxList로 변환하는 오버로딩 된 생성자
	 * </pre>
	 * 
	 * @author In-seong Hwang
	 * @since 2018.11.11
	 * @param list
	 *            변환할 대상의 List 객체
	 */
	@SuppressWarnings("unchecked")
	public HBoxList(List<?> list) {
		if(list != null) {
			Iterator<?> it =list.iterator();
			while(it.hasNext()) {
				this.add((E)it.next());
			}
		}
	}
	/**
	 * <pre>
	 *  add 역할을 할 set
	 * </pre>
	 * 
	 * @author In-seong Hwang
	 * @since 2018.11.11
	 * @param obj
	 *            HBoxList에 추가할 객체
	 */
	public void set (E obj) {
		super.add(obj);
	}
	/**
	 * <pre>
	 *  add 역할을 할 set
	 * </pre>
	 * 
	 * @author In-seong Hwang
	 * @since 2018.11.11
	 * @param objs
	 *            HBoxList에 추가할 E Array
	 */
	public void set (E[] objs) {
		for(E obj : objs) {
			super.add(obj);
		}
	}
	

	
}
