package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Entity for project lookup (fk_project)
 */
@Data
@TableName("fk_project")
public class ReimProject {
    @TableId("project_id")
    private String projectId;
    private String projectNo;
    private String projectName;
}

