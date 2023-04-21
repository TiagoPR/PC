-module(myqueue).
-export([create/0, enqueue/2, dequeue/1, test/0, reverse/1, reverse/2]).

create() -> {[],[]}.  % melhor implementaçáo de uma queue é com uma lista

% enqueue(Queue, Item) -> Queue ++ [Item]   mas vamos implementar raíz 
% enqueue(Queue, Item) -> case Queue of   % FIFO
%     [] -> [Item];
%     [H|T] -> [Item|enqueue(T, H)]     
% end.


% dequeue([]) -> empty;
% dequeue([H|T]) -> {T, H}.


% Maneira mais eficiente 
enqueue({In,Out},Item) -> {[Item|In], Out}.

dequeue({[],[]}) -> empty;
dequeue({In,[Item|T]}) -> {{In, T},Item};
dequeue({In,[]}) -> 
    [H|T] = reverse(In),
    {{[],T}, H}.

% auxiliar para dar reverse à segunda queue
reverse(L) -> reverse(L, []).
reverse([],A) -> A;
reverse([H|T], A) -> reverse(T, [H|A]).


test() ->
    Q1 = create(),
    Q2 = enqueue(Q1,1),
    Q3 = enqueue(Q2,1),
    Q4 = enqueue(Q3,1),
    Q5 = enqueue(Q4,1),
    Q6 = enqueue(Q5,1),
    {Q7, 1} = dequeue(Q6),
    {Q8, 1} = dequeue(Q7),
    {Q9, 1} = dequeue(Q8),
    {Q10, 1} = dequeue(Q9),
    {Q11, 1} = dequeue(Q10),
    empty = dequeue(Q11),
    ok.