package com.easymall.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.easymall.component.RedisComponent;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easymall.entity.enums.PageSize;
import com.easymall.entity.query.SysCategoryQuery;
import com.easymall.entity.po.SysCategory;
import com.easymall.entity.vo.PaginationResultVO;
import com.easymall.entity.query.SimplePage;
import com.easymall.mappers.SysCategoryMapper;
import com.easymall.service.SysCategoryService;
import com.easymall.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("sysCategoryService")
public class SysCategoryServiceImpl implements SysCategoryService {

	@Resource
	private SysCategoryMapper<SysCategory, SysCategoryQuery> sysCategoryMapper;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<SysCategory> findListByParam(SysCategoryQuery param) {
		List<SysCategory> allCategories = this.sysCategoryMapper.selectList(param);
		
		// 将分类按照父子关系组织起来
		return buildCategoryTree(allCategories);
	}
	
	/**
	 * 构建分类树形结构
	 * @param allCategories 所有分类列表
	 * @return 组织好父子关系的分类列表
	 */
	private List<SysCategory> buildCategoryTree(List<SysCategory> allCategories) {
		if (allCategories == null || allCategories.isEmpty()) {
			return allCategories;
		}
		
		// 存储最终的根分类（没有父分类或父分类不在结果集中的分类）
		List<SysCategory> rootCategories = new java.util.ArrayList<>();
		// 用 Map 存储所有分类，方便快速查找
		java.util.Map<String, SysCategory> categoryMap = new java.util.HashMap<>();
		
		// 先将所有分类放入 Map 中
		for (SysCategory category : allCategories) {
			categoryMap.put(category.getCategoryId(), category);
			// 初始化 children 列表
			if (category.getChildren() == null) {
				category.setChildren(new java.util.ArrayList<>());
			}
		}
		
		// 遍历所有分类，构建父子关系
		for (SysCategory category : allCategories) {
			String pCategoryId = category.getpCategoryId();
			
			// 如果没有父 ID，或者父 ID 不在结果集中，则作为根分类
			if (pCategoryId == null || pCategoryId.trim().isEmpty() || !categoryMap.containsKey(pCategoryId)) {
				rootCategories.add(category);
			} else {
				// 找到父分类并添加到其 children 列表中
				SysCategory parent = categoryMap.get(pCategoryId);
				if (parent != null) {
					parent.getChildren().add(category);
				}
			}
		}
		
		return rootCategories;
	}

	@Override
	public void saveCategoryInfo(SysCategory bean) {
		if (bean.getCategoryId() == null) {
			bean.setCategoryId(StringTools.getRandomNumber(5));
			Integer maxSort = this.sysCategoryMapper.selectMaxSort(bean.getpCategoryId());
			bean.setSort(maxSort + 1);
			this.sysCategoryMapper.insert(bean);
		} else {
			this.sysCategoryMapper.updateByCategoryId(bean, bean.getCategoryId());
		}
		//刷新缓存
		save2Redis();
	}

	@Override
	public void delCategory(String categoryId) {
		SysCategoryQuery categoryInfoQuery = new SysCategoryQuery();
		categoryInfoQuery.setCategoryIdOrPCategoryId(categoryId);
		sysCategoryMapper.deleteByParam(categoryInfoQuery);
		//刷新缓存
		save2Redis();
	}

	@Override
	public void changeSort(String categoryIds) {
		String[] categoryIdArray = categoryIds.split(",");
		List<SysCategory> categoryInfoList = new ArrayList<>();
		Integer sort = 1;
		for (String categoryId : categoryIdArray) {
			SysCategory categoryInfo = new SysCategory();
			categoryInfo.setCategoryId(categoryId);
			categoryInfo.setSort(sort++);
			categoryInfoList.add(categoryInfo);
		}
		this.sysCategoryMapper.updateSortBatch(categoryInfoList);
		save2Redis();
	}

	private void save2Redis() {
		SysCategoryQuery categoryInfoQuery = new SysCategoryQuery();
		categoryInfoQuery.setOrderBy("sort asc");
		List<SysCategory> categoryInfoList = findListByParam(categoryInfoQuery);
		redisComponent.saveCategoryList(categoryInfoList);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(SysCategoryQuery param) {
		return this.sysCategoryMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<SysCategory> findListByPage(SysCategoryQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<SysCategory> list = this.findListByParam(param);
		PaginationResultVO<SysCategory> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(SysCategory bean) {
		return this.sysCategoryMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<SysCategory> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.sysCategoryMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<SysCategory> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.sysCategoryMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(SysCategory bean, SysCategoryQuery param) {
		StringTools.checkParam(param);
		return this.sysCategoryMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(SysCategoryQuery param) {
		StringTools.checkParam(param);
		return this.sysCategoryMapper.deleteByParam(param);
	}

	/**
	 * 根据CategoryId获取对象
	 */
	@Override
	public SysCategory getSysCategoryByCategoryId(String categoryId) {
		return this.sysCategoryMapper.selectByCategoryId(categoryId);
	}

	/**
	 * 根据CategoryId修改
	 */
	@Override
	public Integer updateSysCategoryByCategoryId(SysCategory bean, String categoryId) {
		return this.sysCategoryMapper.updateByCategoryId(bean, categoryId);
	}

	/**
	 * 根据CategoryId删除
	 */
	@Override
	public Integer deleteSysCategoryByCategoryId(String categoryId) {
		return this.sysCategoryMapper.deleteByCategoryId(categoryId);
	}
}