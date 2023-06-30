package controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/charge")
@Api(tags = "中断数据接口")
public class StopController {

    public static int stopFlag_1=0;

    @GetMapping(value = "/stop")
    @ApiOperation("中断产生数据")
    public void stop(){
        stopFlag_1=1;
    }
}
