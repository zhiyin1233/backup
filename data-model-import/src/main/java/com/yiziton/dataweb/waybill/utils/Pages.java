package com.yiziton.dataweb.waybill.utils;

import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类，支持Mybatis和JpaQuery
 * 
 * @author lujijiang
 *
 */
public class Pages {

	static Logger logger = LoggerFactory.getLogger(Pages.class);

	/**
	 * 分页接口
	 * 
	 * @author lujijiang
	 *
	 * @param <T>
	 */
	public interface MybatisPage<T> {
		List<T> execute();
	}

	/**
	 * 页面处理器
	 * 
	 * @author lujijiang
	 *
	 * @param <T>
	 */
	public interface PageHandler<T, V> {
		/**
		 * 将实体转换为Vo
		 * 
		 * @param entity
		 * @return
		 */
		V convert(T entity);
	}



	public static <T, V> PageImpl<V> page(Page<T> page, PageHandler<T, V> pageHandler) {
		List<T> entitys = page.getResult();
		List<V> values = new ArrayList<>();
		for (int i = 0; i < entitys.size(); i++) {
			T entity = entitys.get(i);
			V value = pageHandler.convert(entity);
			values.add(value);
		}

		if(page.getPageSize()==0){
			page.setPageSize(10);
		}
		PageRequest pageRequest = new PageRequest(page.getPageNum()-1,page.getPageSize());
		PageImpl<V> pageImpl = new PageImpl<V>(values, pageRequest, page.getTotal());
		return pageImpl;
	}
}
