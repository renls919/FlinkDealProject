package controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.DataService;

@RestController
@RequestMapping(value = "/charge")
@Api(tags = "产生数据接口")
public class DataController {

    @GetMapping(value = "/data/first")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start_period", value = "传感器数据产生时段。单位:小时,取值范围:0~23。"),
            @ApiImplicitParam(name = "dev_id", value = "传感器设备id。如不主动传入,则会自动生成设备id"),
            @ApiImplicitParam(name = "time_duration", value = "模拟数据持续生成时长。单位:分钟。"),
            @ApiImplicitParam(name = "interval", value = "数据产生周期。单位:秒,取值范围:1~60。默认20秒",defaultValue = "20"),
            @ApiImplicitParam(name = "abnormal_weight", value = "温度产生异常的权重。取值:0,1。默认值0。0代表不产生异常,1代表会产生异常",defaultValue ="0"),
            @ApiImplicitParam(name = "abnormal_humweight", value = "湿度产生异常的权重。取值:0,1。默认值0。0代表不产生异常,1代表会产生异常",defaultValue ="0"),
            @ApiImplicitParam(name = "abnormal_CO2weight", value = "CO2浓度产生异常的权重。取值:0,1。默认值0。0代表不产生异常,1代表会产生异常",defaultValue ="0"),
            @ApiImplicitParam(name = "abnormal_O2weight", value = "O2浓度产生异常的权重。取值:0,1。默认值0。0代表不产生异常,1代表会产生异常",defaultValue ="0"),
            @ApiImplicitParam(name = "abnormal_PHweight", value = "PH产生异常的权重。取值:0,1。默认值0。0代表不产生异常,1代表会产生异常",defaultValue ="0"),
            @ApiImplicitParam(name = "abnormal_WPweight", value = "风力产生异常的权重。取值:0,1。默认值0。0代表不产生异常,1代表会产生异常",defaultValue ="0")
    })
    @ApiOperation("产生实时流数据")
    public Object generateData(@RequestParam(value = "start_period") int start_period,
                               @RequestParam(value = "dev_id",required = false) String dev_id,
                       @RequestParam(value = "time_duration") int time_duration,
                       @RequestParam(value = "abnormal_weight",required = false) int abnormal_weight,
                       @RequestParam(value = "abnormal_humweight",required = false) int abnormal_humweight,
                       @RequestParam(value = "interval",required = false) int interval,
                       @RequestParam(value = "abnormal_CO2weight",required = false) int abnormal_CO2weight,
                       @RequestParam(value = "abnormal_O2weight",required = false) int abnormal_O2weight,
                       @RequestParam(value = "abnormal_PHweight",required = false) int abnormal_PHweight,
                       @RequestParam(value = "abnormal_WPweight",required = false) int abnormal_WPweight){
        new DataService().generate(start_period,dev_id,
                time_duration,
                abnormal_weight,
                abnormal_humweight,
                abnormal_CO2weight,
                abnormal_O2weight,
                abnormal_PHweight,
                abnormal_WPweight,
                interval);

        return null;
    }

}
