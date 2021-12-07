#include <stdio.h>

void main(void)
{
    char op;
    float num1, num2, result;
    printf("Enter the operator\n");
    scanf("%c", &op);
    printf("Enter the first number\n");
    scanf("%f", &num1);
    printf("Enter the second number\n");
    scanf("%f", &num2);

    /* Cheack */
    switch(op)
    {
        case '+': result = num1 + num2; break;
        case '-': result = num1 - num2; break;
        case '*': result = num1 * num2; break;
        case '/': result = num1 / num2; break;
        default:
            printf("Error operator!\n");
            result = 0.0;
    }
    /* result  */
    printf("The result = %.2f\n", result);
    printf("press any key to exit");
    fflush(stdin);
    getchar();
}
