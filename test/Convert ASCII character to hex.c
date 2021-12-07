#include <stdio.h>
void main(void){
    char ch;
    printf("Welcome, type 0 to exit u_n\n");
    for(;;){
        printf("> ");
        scanf("%c", &ch);
        fflush(stdin);
        printf("%c -> %x\n", ch, ch);
        if(ch == '0') // exit
            return; // or exit(0);

    }
}
