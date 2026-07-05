package com.cuzssp.campussecondhandtradingplatform_backend.controller.admin;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AdminService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    @Autowired private AdminService adminService;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductImageMapper productImageMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private CategoryMapper categoryMapper;

    @GetMapping
    public Result<?> getProductList(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer status) {
        PageHelper.startPage(page, pageSize);
        List<Product> all;
        if (keyword != null && !keyword.isEmpty())
            all = productMapper.search(keyword);
        else
            all = productMapper.selectAll();
        if (status != null) all = all.stream().filter(p -> p.getStatus().equals(status)).collect(Collectors.toList());
        PageInfo<Product> pgInfo = new PageInfo<>(all);
        List<ProductVO> vos = all.stream().map(p -> {
            ProductVO vo = new ProductVO();
            vo.setId(p.getId()); vo.setUserId(p.getUserId()); vo.setCategoryId(p.getCategoryId());
            vo.setTitle(p.getTitle()); vo.setPrice(p.getPrice()); vo.setStatus(p.getStatus());
            vo.setCampus(p.getCampus()); vo.setCreatedAt(p.getCreatedAt());
            User seller = userMapper.selectById(p.getUserId());
            if (seller != null) vo.setSellerName(seller.getNickname());
            Category cat = categoryMapper.selectById(p.getCategoryId());
            if (cat != null) vo.setCategoryName(cat.getName());
            vo.setImages(productImageMapper.selectByProductId(p.getId()).stream()
                    .map(ProductImage::getUrl).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
        return Result.success(new PageResult<>(vos, pgInfo.getTotal(), pgInfo.getPageNum(), pgInfo.getPageSize()));
    }

    @PutMapping("/{id}/audit")
    public Result<?> auditProduct(@PathVariable Long id, @RequestParam Integer status) {
        return adminService.auditProduct(id, status);
    }
}
