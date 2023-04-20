% This is a test Hello World Erlang Code
-module(hello).
-import(io,[fwrite/1]).
-export([helloworld/0]).

helloworld() ->
   fwrite("Hello, Erlang World!\n").
