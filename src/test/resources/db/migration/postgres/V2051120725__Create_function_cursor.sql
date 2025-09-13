create or replace function cursor(p_sql text)
  returns refcursor
as
$$
declare
   l_ref refcursor;
begin
  open l_ref for execute p_sql;
  return l_ref;
end;
$$
language plpgsql;