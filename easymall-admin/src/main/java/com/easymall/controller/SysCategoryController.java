package com.easymall.controller;

import java.util.List;

import com.easymall.entity.po.SysProductProperty;
import com.easymall.entity.query.SysCategoryQuery;
import com.easymall.entity.po.SysCategory;
import com.easymall.entity.vo.ResponseVO;
import com.easymall.service.SysCategoryService;
import com.easymall.service.SysProductPropertyService;
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
	@Resource
	private SysProductPropertyService sysProductPropertyService;
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
	 * 保存商品属性
	 *
	 * @param productProperty
	 * @return
	 */
	@RequestMapping("/saveProductProperty")
	public ResponseVO saveProductProperty(SysProductProperty productProperty) {
		sysProductPropertyService.saveSysProductPropertyService(productProperty);
		return getSuccessResponseVO(null);
	}

	/**
	 * 删除商品属性
	 *
	 * @param propertyId
	 * @return
	 */
	@RequestMapping("/delProductProperty")
	public ResponseVO delProductProperty(String propertyId) {
		sysProductPropertyService.deleteSysProductPropertyByPropertyId(propertyId);
		return getSuccessResponseVO(null);
	}
}