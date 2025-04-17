separate_arguments(TARGETS)

set(tests_run 0)
set(tests_failed 0)
set(tests_filtered 0)
set(tests_skipped 0)
set(tests_succeeded 0)

option(QUIET "Whether the errors of the tests are output" OFF)
option(STOP_ON_ERROR "Whether to stop on first error" OFF)

foreach(target ${TARGETS})

    if(FILTER)
        if(NOT target MATCHES "${FILTER}")
            math(EXPR tests_filtered "${tests_filtered}+1")
            continue()
        endif()
    endif()
    if ((${tests_failed} GREATER_EQUAL 1) AND ${STOP_ON_ERROR})
        math(EXPR tests_skipped "${tests_skipped}+1")
        continue()
    endif()

    execute_process(COMMAND 
        ${CMAKE_COMMAND} -E env CLICOLOR_FORCE=1
        ${CMAKE_COMMAND} -E cmake_echo_color --magenta --bold "${target}..."
    )


    math(EXPR tests_run "${tests_run}+1")

    execute_process(
        COMMAND "${CMAKE_COMMAND}" --build "${BUILD_DIR}" --target "${target}"
        OUTPUT_VARIABLE output
        ERROR_VARIABLE output
        RESULT_VARIABLE result
    )

    if(${result} EQUAL 0)
        math(EXPR tests_succeeded "${tests_succeeded}+1")

        execute_process(COMMAND 
            ${CMAKE_COMMAND} -E env CLICOLOR_FORCE=1
            ${CMAKE_COMMAND} -E cmake_echo_color --green --bold "=> SUCCESS"
        )

        if(NOT ${QUIET})
            message("")
        endif()

    else()
        math(EXPR tests_failed "${tests_failed}+1")

        execute_process(COMMAND 
            ${CMAKE_COMMAND} -E env CLICOLOR_FORCE=1
            ${CMAKE_COMMAND} -E cmake_echo_color --red --bold "=> FAILURE"
        )

        if(NOT ${QUIET})
            if(output)
                message(${output})
            endif()
        endif()

    endif()

endforeach()

if(${tests_failed} EQUAL 0)

    execute_process(COMMAND 
        ${CMAKE_COMMAND} -E env CLICOLOR_FORCE=1
        ${CMAKE_COMMAND} -E cmake_echo_color --green --bold "==================== SUMMARY ====================\nALL ${tests_run} TESTS HAVE PASSED"
    )

elseif(${STOP_ON_ERROR})
    if (${tests_skipped} GREATER 0)
        set(skipped_test_output  ";   ${tests_skipped} TESTS WERE SKIPPED (due to option -s)")
    endif()
    execute_process(COMMAND 
    ${CMAKE_COMMAND} -E env CLICOLOR_FORCE=1
    ${CMAKE_COMMAND} -E cmake_echo_color --red --bold "==================== SUMMARY ====================\n${tests_succeeded} TESTS HAVE PASSED;    1 TEST HAS FAILED${skipped_test_output}."
    )
else()
    execute_process(COMMAND 
        ${CMAKE_COMMAND} -E env CLICOLOR_FORCE=1
        ${CMAKE_COMMAND} -E cmake_echo_color --red --bold "==================== SUMMARY ====================\n${tests_failed} TESTS HAVE FAILED OUT OF ${tests_run} ${tests_skipped_str}"
    )
endif()
