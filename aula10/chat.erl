-module(chat).
-export([start/1, stop/0]).

start(Port) -> 
    register(?MODULE, spawn(fun() -> server(Port) end)).

stop() -> ?MODULE ! stop.

server(Port) ->
    {ok, LSock} = gen_tcp:listen(Port, [{packet, line}, {reuseaddr, true}]),
    %register(room_manager, spawn(fun() -> rm(#{}) end)),
    RM = spawn(fun() -> rm(#{}) end),
    Room = spawn(fun()-> room([]) end),
    spawn(fun() -> acceptor(LSock, Room, RM) end),
    receive stop -> ok end.

acceptor(LSock, Room, RM) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    spawn(fun() -> acceptor(LSock, Room, RM) end),
    Room ! {enter, self()},
    user(Sock, Room, RM).

get_room(RM, RoomName) ->
    RM ! {get_room, RoomName, self()},
    receive {room, R, RM} -> R end.

rm(Rooms) ->
    receive
        {get_room, RoomName, User} ->
            case maps:find(RoomName, Rooms) of
                {ok, Room} -> 
                    User ! {room,Room, self()},
                    rm(Rooms);
                _ ->
                    Room = spawn(fun()-> room([]) end),
                    User ! {room, Room, self()},
                    rm(Rooms#{RoomName => Room})
            end
    end.



room(Pids) ->
    receive
    {enter, Pid} ->
        io:format("user entered ~n", []),
        room([Pid | Pids]);
    {line, Data} = Msg ->
        io:format("received ~p ~n", [Data]),
        [Pid ! Msg || Pid <- Pids],
        room(Pids);
    {leave, Pid} ->
        io:format("user left ~n", []),
        room(Pids -- [Pid])
end.

user(Sock, Room, RM) ->
    receive
        {line, Data} ->
            gen_tcp:send(Sock, Data),
            user(Sock, Room, RM);
        {tcp, _, Data} ->
            case Data of
                "/room" ++ Rest ->
                    RoomName = Rest -- "\n",
                    NewRoom = get_room(RM, RoomName),
                    Room ! {leave, self()},
                    NewRoom ! {enter, self()};
                _ ->
                    Room ! {line, Data},
                    NewRoom = Room
            end,
            user(Sock, NewRoom, RM);
        {tcp_closed, _} ->
            Room ! {leave, self()};
        {tcp_error, _, _} ->
            Room ! {leave, self()}
end.
