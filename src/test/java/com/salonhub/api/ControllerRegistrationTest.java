package com.salonhub.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
class ControllerRegistrationTest {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    @DisplayName("Every @RestController must have at least one request‐mapping")
    void everyRestControllerHasMappings() {
        // 1) All @RestController beans, unwrapped to their user classes
        Map<String, Object> controllers = ctx.getBeansWithAnnotation(RestController.class);
        Set<Class<?>> controllerTypes = controllers.values().stream()
            .map(AopUtils::getTargetClass)
            .collect(Collectors.toSet());

        // 2) All actually‐mapped handler bean types
        Set<Class<?>> mappedControllers = handlerMapping.getHandlerMethods().values().stream()
            .map(HandlerMethod::getBeanType)
            .collect(Collectors.toSet());

        // 3) Assert each controller appears in the mapped set
        for (Class<?> ctrl : controllerTypes) {
            assertTrue(
                mappedControllers.contains(ctrl),
                () -> "Controller " + ctrl.getSimpleName() + " has no @RequestMapping!"
            );
        }
    }
}