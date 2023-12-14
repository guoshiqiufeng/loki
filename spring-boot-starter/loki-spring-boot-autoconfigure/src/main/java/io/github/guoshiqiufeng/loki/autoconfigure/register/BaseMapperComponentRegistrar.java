/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.loki.autoconfigure.register;

import io.github.guoshiqiufeng.loki.core.mapper.BaseMapper;
import lombok.Setter;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * BaseMapper自动注册类
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 15:38
 */
public class BaseMapperComponentRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {

    /**
     * 环境变量
     */
    @Setter
    private Environment environment;

    /**
     * 资源加载器
     */
    @Setter
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry,
                                        BeanNameGenerator beanNameGenerator) {
        // 获取扫描路径
        Set<String> basePackages = new HashSet<>();
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(LokiMapperScan.class.getName());
        String[] packages = (String[]) annotationAttributes.get("value");
        for (String basePackage : packages) {
            if (StringUtils.hasText(basePackage)) {
                basePackages.add(basePackage);
            }
        }
        if (CollectionUtils.isEmpty(basePackages)) {
            basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        // 扫描接口
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider() {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return true;
            }
        };
        scanner.setEnvironment(environment);
        scanner.setResourceLoader(this.resourceLoader);
        // 过滤只获取实现了 BaseMapper接口的
        scanner.addIncludeFilter(new AssignableTypeFilter(BaseMapper.class));

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidateComponents) {
                if (beanDefinition instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition definition = (AnnotatedBeanDefinition) beanDefinition;
                    AnnotationMetadata annotationMetadata = definition.getMetadata();
                    // 生成代理
                    registrarBaseMapperComponent(annotationMetadata, registry);
                }

            }
        }
    }

    private void registrarBaseMapperComponent(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(BaseMapperFactoryBean.class);
        String className = metadata.getClassName();
        builder.addPropertyValue("mapperInterface", className);

        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        try {
            beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new BeanInstantiationException(this.getClass(), "Cannot found class " + className, e);
        }

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, className);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    /**
     * 构造函数
     */
    public BaseMapperComponentRegistrar() {
        super();
    }
}
