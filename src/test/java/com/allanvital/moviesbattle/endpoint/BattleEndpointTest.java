package com.allanvital.moviesbattle.endpoint;

import com.allanvital.moviesbattle.dto.AnswerDTO;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.allanvital.moviesbattle.utils.JsonString.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BattleEndpointTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Sql(scripts = {"/data/two-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldCreateBattle_WhenGetNewBattleIsInvoked() throws Exception {
        mvc.perform(
                        post("/games/1/battles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.leftBracket.title").exists())
                .andExpect(jsonPath("$.rightBracket.title").exists());
    }

    @Test
    @Sql(scripts = {"/data/three-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnTheSameBattle_WhenTheFirstOneIsNotAnsweredYet() throws Exception {
        MvcResult result = this.performPost();

        Integer leftBracketId = JsonPath.read(result.getResponse().getContentAsString(), "$.leftBracket.id");
        Integer rightBracketId = JsonPath.read(result.getResponse().getContentAsString(), "$.rightBracket.id");

        mvc.perform(
                        post("/games/1/battles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.leftBracket.id").value(leftBracketId))
                .andExpect(jsonPath("$.rightBracket.id").value(rightBracketId));
    }

    @Test
    @Sql(scripts = {"/data/two-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnAnsweredBattle_WhenUserAnswersBattle() throws Exception {
        this.performPost();

        Integer answerId = 1;
        AnswerDTO answer = new AnswerDTO();
        answer.setMovieAnswerId(answerId);

        mvc.perform(
                        patch("/games/1/battles")
                                .characterEncoding("utf-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(asJsonString(answer))
                                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.userAnswer.movieAnswerId").value(answerId));
    }

    @Test
    @Sql(scripts = {"/data/two-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnBattle_WhenGetBattleById() throws Exception {
        this.performPost();
        mvc.perform(
                        get("/games/1/battles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.leftBracket.title").exists())
                .andExpect(jsonPath("$.rightBracket.title").exists())
                .andExpect(jsonPath("$.userAnswer.movieAnswerId").doesNotExist());
    }

    @Test
    @Sql(scripts = {"/data/two-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnBattle_WhenGetBattleByIdAfterAnswer() throws Exception {
        this.performPost();
        Integer answerId = 1;
        this.performPatch(1);

        mvc.perform(
                        get("/games/1/battles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.leftBracket.title").exists())
                .andExpect(jsonPath("$.rightBracket.title").exists())
                .andExpect(jsonPath("$.userAnswer.movieAnswerId").value(1));
    }

    @Test
    @Sql(scripts = {"/data/five-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnError_WhenUsersAsksForNewQuestionInClosedGame() throws Exception {
        mvc.perform(
                        delete("/games/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        AnswerDTO answer = new AnswerDTO();
        answer.setMovieAnswerId(1);

        mvc.perform(
                post("/games/1/battles")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Game is closed, so no more new battles can be provided"));
    }

    @BeforeEach
    private void createBattle()  throws Exception{
        mvc.perform(
                post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    private MvcResult performPost() throws Exception {
        return mvc.perform(
                post("/games/1/battles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andReturn();
    }

    private MvcResult performPatch(Integer answerId) throws Exception {
        AnswerDTO answer = new AnswerDTO();
        answer.setMovieAnswerId(answerId);

        return mvc.perform(
                        patch("/games/1/battles")
                                .characterEncoding("utf-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(asJsonString(answer))
                ).andReturn();
    }

}
