/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cybiks.interconnecting.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTests {

    @Autowired
    private MockMvc mockMvc;

    ////for instance: http://localhost:8080/cybiks/interconnections?departure=DUB&arrival=WRO&departureDateTime=2019-06-12T00:00&arrivalDateTime=2019-06-12T23:00
    @Test
    public void oneDayTest() throws Exception {

        this.mockMvc.perform(get("/interconnections")
                .param("departure", "DUB")
                .param("arrival", "WRO")
                .param("departureDateTime", "2019-06-12T00:00")
                .param("arrivalDateTime", "2019-06-12T23:00")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stops").value("0"))
                .andExpect(jsonPath("$[0].legs[0].departureAirport").value("DUB"))
                .andExpect(jsonPath("$[0].legs[0].arrivalAirport").value("WRO"))
                .andExpect(jsonPath("$[0].legs[0].departureDateTime").value("2019-06-12T16:50:00"))
                .andExpect(jsonPath("$[0].legs[0].arrivalDateTime").value("2019-06-12T19:25:00"));
    }

    @Test
    public void oneDayTestFromPDF() throws Exception {

        this.mockMvc.perform(get("/interconnections")
                .param("departure", "DUB")
                .param("arrival", "WRO")
                .param("departureDateTime", "2018-03-12T07:00")
                .param("arrivalDateTime", "2018-03-03T21:00")).andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void coupleDaysTest() throws Exception {

        this.mockMvc.perform(get("/interconnections")
                .param("departure", "DUB")
                .param("arrival", "WRO")
                .param("departureDateTime", "2019-06-12T00:00")
                .param("arrivalDateTime", "2019-06-15T23:00"))
                .andDo(print()).andExpect(status().isOk());
    }

}
