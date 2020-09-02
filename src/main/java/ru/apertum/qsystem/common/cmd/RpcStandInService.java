/*
 *  Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import ru.apertum.qsystem.common.model.QCustomer;

/**
 * Поставить в очередь к услуге.
 *
 * @author Evgeniy Egorov
 */
public class RpcStandInService extends JsonRPC20 {

    public RpcStandInService() {
    }

    @Expose
    @SerializedName("result")
    private AdvResult result;

    public void setResult(AdvResult result) {
        this.result = result;
    }

    public AdvResult getResult() {
        return result;
    }

    public RpcStandInService(AdvResult result) {
        this.result = result;
    }

    /**
     * Спецкостыль что-бы передать текст ошибки при постановки в очередь предварительного.
     */
    public RpcStandInService(String error) {
        this.result = new AdvResult(error);
    }

    public RpcStandInService(QCustomer customer) {
        this.result = new AdvResult(customer);
    }

    public static class AdvResult {
        @Expose
        @SerializedName("customer")
        private QCustomer customer;

        @Expose
        @SerializedName("error")
        private String error;

        public AdvResult() {
            // for marshaling.
        }

        public AdvResult(QCustomer customer) {
            this.customer = customer;
        }

        AdvResult(String error) {
            this.error = error;
        }

        public QCustomer getCustomer() {
            return customer;
        }

        public String getError() {
            return error;
        }
    }
}
