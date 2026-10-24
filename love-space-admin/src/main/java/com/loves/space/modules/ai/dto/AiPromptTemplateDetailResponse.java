package com.loves.space.modules.ai.dto;

import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 模板详情：全部字段 + active。
 *
 * @param id                 主键
 * @param sceneId            所属场景业务编码
 * @param seq                版本号
 * @param name               名称
 * @param systemPrompt       系统提示词
 * @param userPromptTemplate 输入模板
 * @param variables          变量声明
 * @param remark             修改说明
 * @param active             是否生效中
 * @param tested             当前内容是否已测试
 * @param lastTestInputs     最后一次测试的变量值，可空
 * @param lastTestOutput     最后一次测试的模型原始输出，可空
 * @param lastTestContent    最后一次测试解析出的 JSON，可空
 * @param lastTestModel      最后一次测试实际使用的模型，可空
 * @param lastTestSuccess    最后一次测试是否成功，可空
 * @param lastTestError      最后一次测试失败原因，可空
 * @param lastTestLatencyMs  最后一次测试耗时，可空
 * @param lastTestAt         最后一次测试时间，可空
 * @param lastTestBy         最后一次测试人，可空
 * @param publishedAt        最近发布时间，可空
 * @param publishedBy        最近发布人，可空
 * @param createdBy          创建人
 * @param updatedBy          更新人
 * @param createdAt          创建时间
 * @param updatedAt          更新时间
 * @param version            乐观锁版本
 */
public record AiPromptTemplateDetailResponse(
        UUID id,
        String sceneId,
        int seq,
        String name,
        String systemPrompt,
        String userPromptTemplate,
        List<AiPromptVariable> variables,
        String remark,
        boolean active,
        boolean tested,
        JsonNode lastTestInputs,
        String lastTestOutput,
        JsonNode lastTestContent,
        String lastTestModel,
        Boolean lastTestSuccess,
        String lastTestError,
        Integer lastTestLatencyMs,
        OffsetDateTime lastTestAt,
        String lastTestBy,
        OffsetDateTime publishedAt,
        String publishedBy,
        String createdBy,
        String updatedBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        int version
) {
}
