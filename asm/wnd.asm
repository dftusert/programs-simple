; MSVS2017CE
.386
.model flat, stdcall

ExitProcess proto, exitValue: DWORD
MessageBoxA proto, handle: DWORD, messagePtr: DWORD, titlePtr: DWORD, typeWnd: DWORD
GetStdHandle proto, nStdHandle: DWORD
WriteConsoleA proto, handle: DWORD, lpBuffer: PTR BYTE, nNumberOfBytesToWrite: DWORD, lpNumberOfBytesWritten: PTR DWORD, lpReserved: DWORD

_DATA segment
    _TITLE DB 'Info Title', 0h
    _MESSAGE DB 'Message for MessageBoxA', 0h

    _NEXT_MESSAGE DB 'Message for Console', 0h
    _BYTES_WRITTEN DD ?
    _STD_OUTPUT_HANDLE EQU -11
    _LENGTH EQU $ - _NEXT_MESSAGE
_DATA ends

_TEXT segment
_start:
    invoke GetStdHandle, _STD_OUTPUT_HANDLE

    ; В eax уже записан _CONSOLE_OUT_HANDLE, не нужно его менять
    mov ebx, _LENGTH
    mov ecx, _BYTES_WRITTEN
    mov edx, 0h
    mov esi, offset _NEXT_MESSAGE
    
    invoke WriteConsoleA, eax, esi, ebx, ecx, edx

    mov eax, 0h
    mov edx, 13h
    mov esi, offset _MESSAGE
    mov edi, offset _TITLE

    invoke MessageBoxA, eax, esi, edi, edx

    mov eax, 0h

    invoke ExitProcess, eax
END _start
_TEXT ends
