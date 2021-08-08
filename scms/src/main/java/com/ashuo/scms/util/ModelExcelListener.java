package com.ashuo.scms.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

public class ModelExcelListener extends AnalysisEventListener {
    //根据业务自行处理，可以写入数据库等等
    @Override
    public void invoke(Object data, AnalysisContext context) {
        return;
    }

    //所有的数据解析完了调用
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        return;
    }
}