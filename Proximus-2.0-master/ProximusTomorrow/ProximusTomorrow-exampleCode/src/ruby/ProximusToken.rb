#!/usr/bin/ruby -w
require 'digest/md5'
require 'CGI'

/*
 * @Copyright 2012, Proximus Mobility, LLC., all rights reserved
 * @author dshaw
 */
class ProximusToken
    attr_accessor :my_company_salt

    def initialize(my_company_salt)
        @my_company_salt = my_company_salt
    end

    def generateAuthenticationToken(url)
        toEncode = @my_company_salt + url
	return Digest::MD5.hexdigest(toEncode).upcase
    end
end

tokenGenerator = ProximusToken.new("MYCOMPANYISAWESOME")

URL_BASE = "http://devices.proximusmobility.com/api/"
username = CGI.escape("user@domain.com")
password = "iwadasnin2012wisawuts"

encodingUrl = URL_BASE + username + "/" + password + "/params1/params2/params..n"
token = tokenGenerator.generateAuthenticationToken(encodingUrl)
puts token
requestUrl = URL_BASE + username + "/" + token + "/params1/params2/params..n"

puts "Raw URL was " + encodingUrl
puts "Token would be " + token
puts "Request URL would be " + requestUrl