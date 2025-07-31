MAX_QWORDS: equ 128
MULTIPLY_RESULT_QWORDS: equ 256
section .text

global _start
_start:

    sub rsp, 4 * MAX_QWORDS * 8
    ; первое число [rsp, rsp + MAX]
    ; второе число [rsp + MAX, rsp + 2 * MAX]
    ; результат [rsp + 2 * MAX, rsp + 4 * MAX]

    mov rdi, rsp
    mov rcx, MAX_QWORDS
    call read_long

    lea rdi, [rsp + MAX_QWORDS * 8]
    call read_long

    lea rdi, [rsp + 2 * MAX_QWORDS * 8]
    mov rcx, MULTIPLY_RESULT_QWORDS
    call set_zero

    lea rdi, [rsp + 2 * MAX_QWORDS * 8]
    lea rsi, [rsp + MAX_QWORDS * 8]
    mov rdx, rsp
    mov rcx, MAX_QWORDS
    call mul_long_long

    lea rdi, [rsp + 2 * MAX_QWORDS * 8]
    mov rcx, MULTIPLY_RESULT_QWORDS
    call write_long

    mov al, 0x0a
    call write_char

    jmp exit


; mul two long numbers
;    rdx -- address of factor #1 (long number)
;    rsi -- address of factor #2 (long number)
;    rdi -- adress of result
;    rcx -- length of long numbers in qwords
; result:
;    multiply is written to rdi
mul_long_long:
    push rdx
    push rsi
    push rdi
    push rcx

    mov r8, rdi ; result index
    mov r9, rdx ; i - index of A
    mov r10, rcx ; counter for cycle on A

.loop:
    mov r13, [r9] ; r13 = A_i

    xor r11, r11
    mov r14, rsi ; j - index of B
    mov r15, r8 ; Res_i+j

    mov r12, rcx
.inner_loop:
    mov rax, [r14] ; rax = B_j

    mul r13 ; rdx:rax = A_i * B_j

    ; r15 <-> Res_i+j = Res_i+j rax + r9 c учетом переносов
    ; rdx - перенос на следующий разряд
    add [r15], rax
    adc rdx, 0 ; eсли переполнилось, то добавляем 1 к переносу
    add [r15], r11
    adc rdx, 0 ; аналогично если переполнилось после прибавления переноса с прошлого разряда
    mov r11, rdx

    lea r14, [r14 + 8]
    lea r15, [r15 + 8]

    dec r12
    jnz .inner_loop

    mov [r15], r11

    lea r8, [r8 + 8]
    lea r9, [r9 + 8]

    dec r10
    jnz .loop

    pop rcx
    pop rdi
    pop rsi
    pop rdx

    ret

; adds two long numbers
;    rdi -- address of summand #1 (long number)
;    rsi -- address of summand #2 (long number)
;    rcx -- length of long numbers in qwords
; result:
;    sum is written to rdi
add_long_long:
    push rdi
    push rsi
    push rcx

    clc
.loop:
    mov rax, [rsi]
    lea rsi, [rsi + 8]
    adc [rdi], rax
    lea rdi, [rdi + 8]
    dec rcx
    jnz .loop

    pop rcx
    pop rsi
    pop rdi
    ret

; adds a short number to a long number
;    rdi -- address of summand #1 (long number)
;    rax -- summand #2 (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    sum is written to rdi
add_long_short:
    push rdi
    push rcx
    push rdx

    xor rdx, rdx
.loop:
    add [rdi], rax
    adc rdx, 0
    mov rax, rdx
    xor rdx, rdx
    add rdi, 8
    dec rcx
    jnz .loop

    pop rdx
    pop rcx
    pop rdi
    ret

; multiplies a long number by a short number
;    rdi -- address of multiplier #1 (long number)
;    rbx -- multiplier #2 (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    product is written to rdi
mul_long_short:
    push rax
    push rdi
    push rcx

    xor rsi, rsi
.loop:
    mov rax, [rdi]
    mul rbx
    add rax, rsi
    adc rdx, 0
    mov [rdi], rax
    add rdi, 8
    mov rsi, rdx
    dec rcx
    jnz .loop

    pop rcx
    pop rdi
    pop rax
    ret

; divides a long number by a short number
;    rdi -- address of dividend (long number)
;    rbx -- divisor (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    quotient is written to rdi
;    remainder is written to rdx
div_long_short:
    push rdi
    push rax
    push rcx

    lea rdi, [rdi + 8 * rcx - 8]
    xor rdx, rdx

.loop:
    mov rax, [rdi]
    div rbx
    mov [rdi], rax
    sub rdi, 8
    dec rcx
    jnz .loop

    pop rcx
    pop rax
    pop rdi
    ret

; assigns zero to a long number
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
set_zero:
    push rax
    push rdi
    push rcx

    xor rax, rax
    rep stosq

    pop rcx
    pop rdi
    pop rax
    ret

; checks if a long number is zero
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
; result:
;    ZF=1 if zero
is_zero:
    push rax
    push rdi
    push rcx

    xor rax, rax
    rep scasq

    pop rcx
    pop rdi
    pop rax
    ret

; reads a long number from stdin
;    rdi -- location for output (long number)
;    rcx -- length of long number in qwords
read_long:
    push rcx
    push rdi

    call set_zero
.loop:
    call read_char
    or rax, rax
    js exit
    cmp rax, 0x0a
    je .done
    cmp rax, '0'
    jb .invalid_char
    cmp rax, '9'
    ja .invalid_char

    sub rax, '0'
    mov rbx, 10
    call mul_long_short
    call add_long_short
    jmp .loop

.done:
    pop rdi
    pop rcx
    ret

.invalid_char:
    mov rsi, invalid_char_msg
    mov rdx, invalid_char_msg_size
    call print_string
    call write_char
    mov al, 0x0a
    call write_char

.skip_loop:
    call read_char
    or rax, rax
    js exit
    cmp rax, 0x0a
    je exit
    jmp .skip_loop

; writes a long number to stdout
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
write_long:
    push rax
    push rcx

    mov rax, 20
    mul rcx
    mov rbp, rsp
    sub rsp, rax

    mov rsi, rbp

.loop:
    mov rbx, 10
    call div_long_short
    add rdx, '0'
    dec rsi
    mov [rsi], dl
    call is_zero
    jnz .loop

    mov rdx, rbp
    sub rdx, rsi
    call print_string

    mov rsp, rbp
    pop rcx
    pop rax
    ret

; reads one char from stdin
; result:
;    rax == -1 if error occurs
;    rax \in [0; 255] if OK
read_char:
    push rcx
    push rdi

    sub rsp, 1
    xor rax, rax
    xor rdi, rdi
    mov rsi, rsp
    mov rdx, 1
    syscall

    cmp rax, 1
    jne .error
    xor rax, rax
    mov al, [rsp]
    add rsp, 1

    pop rdi
    pop rcx
    ret
.error:
    mov rax, -1
    add rsp, 1
    pop rdi
    pop rcx
    ret

; writes one char to stdout, errors are ignored
;    al -- char
write_char:
    sub rsp, 1
    mov [rsp], al

    mov rax, 1
    mov rdi, 1
    mov rsi, rsp
    mov rdx, 1
    syscall
    add rsp, 1
    ret

exit:
    mov rax, 60
    xor rdi, rdi
    syscall

; prints a string to stdout
;    rsi -- string
;    rdx -- size
print_string:
    push rax

    mov rax, 1
    mov rdi, 1
    syscall

    pop rax
    ret


section .rodata
invalid_char_msg:
    db "Invalid character: "
invalid_char_msg_size: equ $ - invalid_char_msg