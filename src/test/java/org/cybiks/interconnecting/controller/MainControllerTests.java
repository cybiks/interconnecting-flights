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
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTests {

    @Autowired
    private MockMvc mockMvc;


    ////for example: http://localhost:8080/cybiks/interconnections?departure=DUB&arrival=WRO&departureDateTime=2019-06-12T00:00&arrivalDateTime=2019-06-12T23:00
    @Test
    public void paramGreetingShouldReturnTailoredMessage() throws Exception {

        ResultActions resultActions = this.mockMvc.perform(get("/interconnections").param("departure", "DUB")
                .param("arrival", "WRO")
                .param("departureDateTime", "2019-06-12T00:00")
                .param("arrivalDateTime", "2019-06-12T23:00")).andDo(print());
//                .andDo(print()).andExpect(status().isOk());
//                .andExpect(jsonPath("$.departure").value("DUB"))
//                .andExpect(jsonPath("$.arrival").value("WRO"))
//                .andExpect(jsonPath("$.departureDateTime").value("2019-06-12T07:00:00"))
//                .andExpect(jsonPath("$.arrivalDateTime").value("2019-06-12T21:00:00"));
    }

}
