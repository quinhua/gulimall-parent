package pers.qh.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 全局统一返回结果类
 */
@Data
@ApiModel(value = "全局统一返回结果")
public class ResultVo<T> {

    // 200 , 成功!
    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private T data;

    public ResultVo(){}

    // 返回数据
    protected static <T> ResultVo<T> build(T data) {
        ResultVo<T> resultVo = new ResultVo<T>();
        if (data != null)
            resultVo.setData(data);
        return resultVo;
    }

    public static <T> ResultVo<T> build(T body, ResultCodeEnum resultCodeEnum) {
        ResultVo<T> resultVo = build(body);
        resultVo.setCode(resultCodeEnum.getCode());
        resultVo.setMessage(resultCodeEnum.getMessage());
        return resultVo;
    }

    public static<T> ResultVo<T> ok(){
        return ResultVo.ok(null);
    }

    /**
     * 操作成功
     * @param data
     * @param <T>
     * @return
     */
    public static<T> ResultVo<T> ok(T data){
        ResultVo<T> resultVo = build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> ResultVo<T> fail(){
        return ResultVo.fail(null);
    }

    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static<T> ResultVo<T> fail(T data){
        ResultVo<T> resultVo = build(data);
        return build(data, ResultCodeEnum.FAIL);
    }

    public ResultVo<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public ResultVo<T> code(Integer code){
        this.setCode(code);
        return this;
    }

    public boolean isOk() {
        if(this.getCode().intValue() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            return true;
        }
        return false;
    }
}
