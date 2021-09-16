package me.bytebeats.mns.meta;

import java.util.List;

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2020/10/8 14:48
 * @Version 1.0
 * @Description FundFirmOp is used for parsing jsonp result from server
 */

public class FundFirmOp {
    List<List<String>> op;

    public List<List<String>> getOp() {
        return op;
    }

    public void setOp(List<List<String>> op) {
        this.op = op;
    }
}
