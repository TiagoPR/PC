-module(login_manager).
-export([start/0, 
        loop/1,
        create_account/2, 
        close_account/2, 
        login/2, 
        logout/1, 
        online/0]).

start() -> 
    %register(login_manager, spawn(fun() -> loop(#{}) end)).
    register(?MODULE, spawn(fun() -> loop(#{}) end)).

invoke(Request) ->                      % auxiliar
    ?MODULE ! {Request, self()},
    receive 
        {Res, ?MODULE} -> Res 
    end.

create_account(User, Pass) -> invoke({create_account, User, Pass}).

close_account(User, Pass) -> invoke({close_account, User, Pass}).

login(User, Pass) -> invoke({login, User, Pass}).

logout(User) -> invoke({logout, User}).

online() -> invoke(online).


% create_account(User, Pass) ->
%     ?MODULE ! {create_account, User, Pass, self()},
%     receive
%         {Res, ?MODULE} -> Res % resultado do create_account
%     end.

% close_account(User, Pass) ->
%     ?MODULE ! {close_account, User, Pass, self()},
%     receive
%         {Res, ?MODULE} -> Res % resultado do create_account
%     end.


handle({create_account, User, Pass}, Map) ->        % auxiliar
    case maps:find(User,Map) of
        error ->
            {ok, Map#{User => {Pass, false}}};
        _ -> 
           {user_exists, Map}
    end;

handle({close_account, User, Pass}, Map) ->
    case maps:find(User,Map) of
        {ok, {Pass, _}} ->
            {ok, maps:remove(User, Map)};
        _ -> 
            {invalid, Map}
    end;

handle({login, User, Pass}, Map) ->
    case maps:find(User,Map) of
        {ok, {Pass, false}} ->
            {ok, Map#{User := {Pass, true}}};
            %{ok, maps:update(User, {Pass, true}, Map)};
        _ -> 
            {invalid, Map}
    end;

handle({logout, User}, Map) -> 
    case maps:find(User,Map) of
        {ok, {Pass, true}} ->
            {ok, Map#{User := {Pass, false}}};
            %{ok, maps:update(User, {Pass, true}, Map)};
        _ -> 
            {invalid, Map}
    end;

handle(online,Map) ->
    Res = [ User || {User, {_,true}} <- maps:to_list(Map)],
    {Res, Map}.

loop(Map) ->
    receive {Request, From} ->
        {Res, NextState} = handle(Request, Map),
        From ! {Res, ?MODULE},
        loop(NextState)
    end.

% loop(Map) ->
%     receive
%         {{create_account, User, Pass}, From} ->
%             case maps:find(User,Map) of
%                 error ->
%                     From ! {ok, ?MODULE},
%                     %Map1 = maps:put(User, {Pass, false}, Map},
%                     %Map1 = Map#{User => {Pass, false}},
%                     %loop(Map1);
%                     loop(Map#{User => {Pass, false}});

%                 {ok, _} -> 
%                     From ! {user_exists, ?MODULE},
%                     loop(Map)
%             end;
%         {{close_account, User, Pass}, From} ->
%             case maps:find(User,Map) of
%                 {ok, {Pass, _}} ->
%                     From ! {ok, ?MODULE},
%                     loop(maps:remove(User,Map));

%                 _ -> 
%                     From ! {invalid, ?MODULE},
%                     loop(Map)
%             end
                 
%     end.