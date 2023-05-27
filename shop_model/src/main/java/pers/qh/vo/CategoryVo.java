package pers.qh.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CategoryVo {
    //当前分类id
    private Long categoryId;
    //分类名称
    private String categoryName;
    //子分类信息
    private List<CategoryVo> categoryChild;
}
