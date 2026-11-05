package com.paysense;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class WalletApiTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json;
 private String token(String email)throws Exception{
  String body="{\"name\":\"Jordan Davis\",\"email\":\""+email+"\",\"password\":\"strong-pass-123\"}";
  String result=mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  return json.readTree(result).get("accessToken").asText();
 }
 @Test void userCanRegisterAndReadProtectedWallet()throws Exception{
  String token=token("register@example.com");
  mvc.perform(get("/api/dashboard").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(12840.5)).andExpect(jsonPath("$.currency").value("USD"));
 }
 @Test void transferIsIdempotentAndCannotOverdraw()throws Exception{
  String auth="Bearer "+token("transfer@example.com");
  mvc.perform(post("/api/transfers").header("Authorization",auth).header("Idempotency-Key","first-send").contentType(MediaType.APPLICATION_JSON).content("{\"recipient\":\"Alex Morgan\",\"amount\":25.50}"))
    .andExpect(status().isCreated()).andExpect(jsonPath("$.transaction.amount").value(-25.5)).andExpect(jsonPath("$.balance").value(12815.0));
  mvc.perform(post("/api/transfers").header("Authorization",auth).header("Idempotency-Key","first-send").contentType(MediaType.APPLICATION_JSON).content("{\"recipient\":\"Alex Morgan\",\"amount\":25.50}"))
    .andExpect(status().isCreated()).andExpect(jsonPath("$.transaction.amount").value(-25.5));
  mvc.perform(post("/api/transfers").header("Authorization",auth).contentType(MediaType.APPLICATION_JSON).content("{\"recipient\":\"Alex Morgan\",\"amount\":999999.00}"))
    .andExpect(status().isUnprocessableEntity());
 }
 @Test void protectedEndpointsRejectAnonymousRequests()throws Exception{mvc.perform(get("/api/transactions")).andExpect(status().isUnauthorized());}
}
