package com.loves.space.modules.ai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 新建提示词模板请求（seq 由服务端分配）。
 *
 * @param sceneId            所属场景业务编码（必填）
 * @param name               模板名称（必填，≤64）
 * @param systemPrompt       系统提示词（必填）
 * @param userPromptTemplate 输入模板（必填）
 * @param variables          变量声明（可空，默认空数组）
 * @param remark             修改说明（可空）
 */
public record AiPromptTemplateCreateRequest(
        @NotBlank(message = "所属场景不能为空") String sceneId,
        @NotBlank(message = "模板名称不能为空") @Size(max = 64, message = "模板名称长度不能超过 64 个字符") String name,
        @NotBlank(message = "系统提示词不能为空") String systemPrompt,
        @NotBlank(message = "输入模板不能为空") String userPromptTemplate,
        List<@Valid AiPromptVariable> variables,
        String remark
) {
}
