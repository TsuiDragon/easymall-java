package com.easymall.controller;

import java.util.List;

import com.easymall.entity.query.SysCategoryQuery;
import com.easymall.entity.po.SysCategory;
import com.easymall.entity.vo.ResponseVO;
import com.easymall.service.SysCategoryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 *  Controller
 */
@RestController("sysCategoryController")
@RequestMapping("/sysCategory")
public class SysCategoryController extends ABaseController{

	@Resource
	private SysCategoryService sysCategoryService;
	/**
	 * 查询
	 */
	@RequestMapping("/loadCategory")
	public ResponseVO loadDataList(){
		SysCategoryQuery param = new SysCategoryQuery();
		return getSuccessResponseVO(sysCategoryService.findListByParam(param));
	}


	/**
	 * 新增或修改
	 */
	@RequestMapping("/saveCategory")
	public ResponseVO saveCategory(SysCategory category) {
		sysCategoryService.saveCategoryInfo(category);
		return getSuccessResponseVO(null);
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delCategory")
	public ResponseVO delCategory(String categoryId) {
		sysCategoryService.delCategory(categoryId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 修改排序
	 */
	@RequestMapping("/changeCategorySort")
	public ResponseVO changeCategorySort(String categoryIds) {
		sysCategoryService.changeSort(categoryIds);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CategoryId查询对象
	 */
	@RequestMapping("/getSysCategoryByCategoryId")
	public ResponseVO getSysCategoryByCategoryId(String categoryId) {
		return getSuccessResponseVO(sysCategoryService.getSysCategoryByCategoryId(categoryId));
	}

	/**
	 * 根据CategoryId修改对象
	 */
	@RequestMapping("/updateSysCategoryByCategoryId")
	public ResponseVO updateSysCategoryByCategoryId(SysCategory bean,String categoryId) {
		sysCategoryService.updateSysCategoryByCategoryId(bean,categoryId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CategoryId删除
	 */
	@RequestMapping("/deleteSysCategoryByCategoryId")
	public ResponseVO deleteSysCategoryByCategoryId(String categoryId) {
		sysCategoryService.deleteSysCategoryByCategoryId(categoryId);
		return getSuccessResponseVO(null);
	}
}